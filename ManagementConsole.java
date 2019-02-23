/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Maintains a blocked list and listens to the manager's requests to block urls, list blocked urls or shut down the proxy
*/

//imports
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.Thread;

class ManagementConsole
{
  //constants
  private static final int DEFAULT_PORT = 4000;

  //class variables
  private static HashSet<String> blockedURLs = new HashSet<String>();


  /**
   * Program initiation method. Accepts requests to block certain servers, and runs the web proxy server on another thread
   *
   * @param args: The input to the program while running it
   *
   * @return: None
   */
  public static void main(String[] args)
  {
    //start managing the server and blocked lists
    start_managing();

    System.out.println("We're done :D");
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
    System.out.println("2. Enter 'list blocked' to check which URLs are blocked");
    System.out.println("3. Enter 'e' to indicate that you want to exit");

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

      //Case 3: adding a blocked url if it isn't already blocked
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
        {
          blockedURLs.add(input);
          System.out.println("Done");
        }
      }

      //Case 4: Invalid input
      else
        System.out.println("Sorry, I didn't get that");

    }

    //shut down the web proxy and all request threads
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
   * Checks if a site is blocked or not
   *
   * @param url: The url whose blockage being checked
   *
   * @return: True if site is blocked, else false
   */
  public static boolean blocked(String url)
  {
    url = formatURL(url);

    return blockedURLs.contains(url);
  }
}
