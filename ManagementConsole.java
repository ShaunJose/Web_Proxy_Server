/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.Thread;

class ManagementConsole
{

  //class variables
  public static HashSet<String> blockedURLs = new HashSet<String>();

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
    System.out.println("3. Enter 'e' to indicate that you're done listing URLs");

    //create variables needed for block url processing
    Scanner sc = new Scanner(System.in);
    boolean addingBlockedSites = true;

    //Accepting blocked lists
    while(addingBlockedSites)
    {
      //get input
      String input = sc.nextLine();
      //TODO: convert to lower case

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

        //add http:// if required
        if(!input.contains("http://") && !input.contains("https://"))
          input = "http://" + input; //might not be secure, so only http

        //if URL is invalid
        if(!isValidUrl(input))
          System.out.println("This URL is invalid");
        //if URL already blocked
        else if(blockedURLs.contains(input))
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

    //make the server start listening
    WebProxy.start_listening();
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
  public static boolean isValidUrl(String url)
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
}
