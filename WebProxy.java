/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Creates a new thread for all client requests, and handles the base socket
*/

//imports
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class WebProxy implements Runnable
{
  //class variables
  private int port;
  private HashSet<Thread> requestThreads;
  private boolean open;
  private ServerSocket serverSocket;

  /**
   * Creates a web proxy object. Intiialises the class variables
   *
   * @param port: The port at which you want the WebProxy  server to listen
   */
  WebProxy(int port)
  {
    this.port = port;
    requestThreads = new HashSet<Thread>();
    this.open = true; //server is open

    try
    { serverSocket = new ServerSocket(this.port); }
    catch(Exception e)
    { System.out.println("Couldn't create server socket for proxy"); }
  }

  /**
   * Open a Socket and start listening on port 4000
   *
   * @return: None
   */
  private void start_listening()
  {
    try
    {
      System.out.println("Listening on port " + this.port + "...\n");

      do
      {
        handleRequest(serverSocket.accept());
      } while(this.open);
    }

    catch(Exception e)
    {
      System.out.println("Not accepting any more client requests!");
    }

  }


  /**
   * Handles a request by making reqHandler object run as another thread
   *
   * @param clientSocket: The socket for the relevant request
   *
   * @return: None
   */
  private void handleRequest(Socket clientSocket)
  {
    //Create request handler object and make it run on a thread
    RequestHandler reqHandler = new RequestHandler(clientSocket);
    Thread reqThread = new Thread(reqHandler);
    reqThread.start(); //start the thread

    //save the thread here
    requestThreads.add(reqThread);
  }


   /**
    * //TODO: ADD FUNC DESCR HERE
    */
   public void shutDown()
   {
     //TODO: close SERVER SOCKET HERE, along with all request threads
     this.open = false;

     try
     { serverSocket.close(); }
     catch(Exception e)
     { System.out.println("Failed to close proxy server socket :("); }

   }


  /**
   * Thread of WebProxy object starts here. Just makes the proxy start listening
   *
   * @return: None
   */
  @Override
  public void run()
  {
    start_listening();
  }

}
