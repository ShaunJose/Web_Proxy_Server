/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Maintains a blocked list, cache and listens to the manager's requests to block urls, list blocked urls or shut down the proxy
*/

//imports
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.Thread;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

class ManagementConsole
{
  //constants
  private static final int DEFAULT_PORT = 4000;
  private static final int CACHE_LIMIT = 2;
  private static final String CACHE_FILE = "cache.txt";
  private static final String BLOCKED_FILE = "blocked.txt";
  private static final String FILE_DELIMITER = "---***^***^^^^^^***^***---";

  //class variables
  private static HashSet<String> blockedURLs = new HashSet<String>();
  private static ArrayList<String> cachedURLs = new ArrayList<String>();
  private static ArrayList<String> cachedResponses = new ArrayList<String>();


  /**
   * Program initiation method. Accepts requests to block certain servers, and runs the web proxy server on another thread
   *
   * @param args: The input to the program while running it
   *
   * @return: None
   */
  public static void main(String[] args)
  {
    //initialise the cache
    initCache();

    //initialises the set of blocked URLs
    initBlockedSet();

    //start managing the server and blocked lists
    start_managing();

    System.out.println("We're done :D");
  }


  /**
   * Create the cache file if it doesnt exist. If exists, call readFromcache to read cache contents into the cache variables
   *
   * @return: None
   */
  private static void initCache()
  {
    try
    {
      File cache = new File(CACHE_FILE); //cache path
      if(cache.exists()) //if cache exists, read from file
      {
        readFromCache(cache);
      }
      else //if it doesn't exist create new empty cache file
      {
        cache.createNewFile();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }


  /**
   * Reads from filepath into cache variables, to initialise cache vars
   *
   * @param cache: Filepath to cache
   */
  private static void readFromCache(File cache)
  {
    //initialise vars used for this task in loop
    Scanner sc;
    try
    {sc = new Scanner(cache);} catch(Exception e){ e.printStackTrace();return; }
    String line = "";

    //read urls and responses into arrays
    while(sc.hasNext())
    {
      //read url
      line = sc.nextLine();
      cachedURLs.add(line);

      //read response
      String response = "";
      line = ""; //reset line
      do
      {
        response += line;
        line = sc.nextLine() + "\r\n";
      } while (!line.equals(FILE_DELIMITER + "\r\n"));
      cachedResponses.add(response);

    }
  }


  /**
   * Writes all cache contents into the file, follwing the format rules
   *
   * @return: None
   */
  private static void saveCache()
  {
    //initialise variables used to write cache
    PrintWriter cacheWriter = null;
    try
    { cacheWriter = new PrintWriter(CACHE_FILE, "UTF-8"); }
    catch(Exception e) { e.printStackTrace(); return; }

    for(int i = 0; i < cachedURLs.size(); i++)
    {
      //save url
      String url = cachedURLs.get(i);
      cacheWriter.println(url);

      //save response
      String response = cachedResponses.get(i);
      cacheWriter.print(response);

      //delimiter!
      cacheWriter.println(FILE_DELIMITER);
    }

    cacheWriter.close();

  }


  /**
   */
  private static void initBlockedSet()
  {
    try
    {
      File blocked = new File(BLOCKED_FILE); //file path
      if(blocked.exists()) //if file exists, read from file
      {
        readFromBlocked(blocked);
      }
      else //if it doesn't exist create new empty blocked file
      {
        blocked.createNewFile();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }


  /**
   */
  private static void readFromBlocked(File blocked)
  {
    //initialise vars used for this task in loop
    Scanner sc;
    try
    { sc = new Scanner(blocked);} catch(Exception e){ e.printStackTrace();return; }

    while(sc.hasNextLine())
    {
      String blockedURL = sc.nextLine();
      blockedURLs.add(blockedURL);
    }

  }


  /**
   */
  private static void saveBlockedList()
  {
    //initialise variables used to write cache
    PrintWriter blockedWriter = null;
    try
    { blockedWriter = new PrintWriter(BLOCKED_FILE, "UTF-8"); }
    catch(Exception e) { e.printStackTrace(); return; }
    Iterator blockedIter = blockedURLs.iterator();

    //save blocked urls to file
    while(blockedIter.hasNext())
    {
      String url = (String) blockedIter.next();
      blockedWriter.println(url);
    }

    blockedWriter.close();
  }


  /**
   * Manages blockedUrls set, stores it in a file, and calls the        start_listening() method to start running a server
   *
   * @return: None
   */
  private static void start_managing()
  {
    //Instructions for managing urls
    System.out.println("Instructions:");
    System.out.println("1. Enter 'block URL_name' to block a URL");
    System.out.println("2. Enter 'unblock URL_name' to unblock a URL");
    System.out.println("3. Enter 'list blocked' to check which URLs are blocked");
    System.out.println("4. Enter 'e' to indicate that you want to exit");

    //make Web proxy server run on another thread
    WebProxy proxy = new WebProxy(DEFAULT_PORT);
    Thread proxyThread = new Thread(proxy);
    proxyThread.start();

    //create variables needed for block url processing
    Scanner sc = new Scanner(System.in);
    boolean addingBlockedSites = true;

    //Accepting blocked lists
    while(addingBlockedSites)
    {
      //get input in lower case, so as to not add duplicates just because of case difference
      String input = sc.nextLine().toLowerCase();

      //check input and act accordingly
      //Case 1: exit
      if(input.equals("e"))
        addingBlockedSites = false;

      //Case 2: list the blocked urls
      else if(input.equals("list blocked"))
        displayHashSet(blockedURLs);

      //Case 3: Blocking a url
      else if(input.length() > 6 && input.substring(0, 6).equals("block "))
      {
        //get url part of input
        input = input.substring(6);

        input = formatURL(input);

        //if URL is invalid
        if(!isValidUrl(input))
          System.out.println("This URL is invalid");
        //if URL already blocked
        else if(blocked(input))
          System.out.println("This URL has already been blocked.");
        //block the URL
        else
        { //hostName and not entire url
          blockedURLs.add(input.substring(input.indexOf("//") + 2));
          System.out.println("Blocked");
        }
      }

      //Case 4: Unblocking a url
      else if(input.length() > 8 && input.substring(0, 8).equals("unblock "))
      {
        //get url part of input
        input = formatURL(input.substring(8));

        //if URL is invalid
        if(!isValidUrl(input))
          System.out.println("This URL is invalid");
        //if URL is not blocked
        else if(!blocked(input))
          System.out.println("This URL is not blocked.");
        //block the URL
        else
        { //hostName and not entire url
          blockedURLs.remove(input.substring(input.indexOf("//") + 2));
          System.out.println("Unblocked");
        }
      }
      //Case 5: Invalid input
      else
        System.out.println("Sorry, I didn't get that");

      }

    //save cache, blockedURLs and shut down the web proxy completely
    saveCache();
    saveBlockedList();
    proxy.shutDown();

    //end the thread
    try
    {
      proxyThread.join();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }


  /**
   * Displays a String type HashSet
   *
   * @param set: The set who's elements need to be displayed
   *
   * @return: None
   */
  public static void displayHashSet(HashSet<String> set)
  {
    //standard null check
    if(set == null)
      return;

    //Iterate over String elements and print them
    for(String element : set)
      System.out.println(element);
  }


  /**
   * Checks if the given url string is valid
   *
   * @param url: The String url whose validity has to be checked
   *
   * @return: true indicating URL is valid, false otherwise
   */
  private static boolean isValidUrl(String url)
  {

    try
    {
      new URL(url).toURI();
      return true;
    }
    catch(Exception e)
    {
      return false;
    }

  }

  /**
   * Adds http or https to the String passed if it doesnt exist
   *
   * @param url: The url that has to be formatted (type String)
   *
   * @return: The formatted url
   */
  private static String formatURL(String url)
  {
    //add http:// if needed
    if(!url.contains("http://") && !url.contains("https://"))
      url = "http://" + url; //might not be secure, so only http

    return url;
  }


  /**
   * Checks if a url has been cached and return true or false indicating whether it exists or not
   *
   * @param url: The url that you want to check has been cached
   *
   * @return: true if url is cached, else false
   */
  public static boolean isCached(String url)
  {
    url = formatURL(url);

    return cachedURLs.contains(url);
  }


  /**
   * Gets a response for a cached url from the cache. Null if not cached
   *
   * @param url: The url whose response is needed
   *
   * @return: Cached response fro url, or null if doesnt exist in cache
   */
  public static String getFromCache(String url)
  {
    url = formatURL(url);

    if(!isCached(url)) //if in cache
      return null;

    int index = cachedURLs.indexOf(url);
    return cachedResponses.get(index);
  }


  /**
   * Saves url and response to cache, as most recently added cache. Recursive function
   *
   * @param url: The url whose repsonse needs to be cached
   * @param response: The response which needs to be cached
   *
   * @return: None
   */
  public static void saveToCache(String url, String response)
  {
    url = formatURL(url);

    if(isCached(url)) //if already cached, replace as Newest elements!
    {
      int index = cachedURLs.indexOf(url);
      removeFromCache(index);
      saveToCache(url, response); //recursively save it
      return; //end
    }

    if(cachedURLs.size() >= CACHE_LIMIT) //if cache is full, remove LRUs
    {
      removeFromCache(0); //remove least recently used element
      saveToCache(url, response); //recursively add
      return; //end
    }

    //reaches here if not duplicate and cache not full
    cachedURLs.add(url);
    cachedResponses.add(response);
  }


  /**
   * Delete cahced element at index from cached urls and cahced responses
   *
   * @param index: index at which elements need to be deleted
   *
   * @return: None
   */
  private static void removeFromCache(int index)
  {
    if(index < CACHE_LIMIT) //private arr, so trusting that limit isn't crossed
    {
      cachedURLs.remove(index);
      cachedResponses.remove(index);
    }
  }


  /**
   * Checks if a site is blocked or not
   *
   * @param url: The url whose blockage being checked
   *
   * @return: True if site is blocked, else false
   */
  public static boolean blocked(String url)
  {
    url = formatURL(url); //to ensure that is has http:// or https://

    return blockedURLs.contains(url.substring(url.indexOf("//") + 2));
  }
}
