/* author: Shaun Jose
   github: github.com/ShaunJose
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
      ServerSocket serverSocket = new ServerSocket(this.port);
      System.out.println("Listening on port " + this.port + "...");

      do
      {
        handleRequest(serverSocket.accept());
      } while(this.open);
      // System.out.println(socket.getLocalSocketAddress());
      // System.out.println(socket.getRemoteSocketAddress());
      // System.out.println(socket.toString());
      // System.out.println(socket.getPort());
      // System.out.println(socket.getInetAddress().getHostName());
      // System.out.println(socket.getInetAddress().getLocalHost());
      // System.out.println(socket.getInetAddress().getHostAddress());
      // System.out.println(socket.localport);
    }
    catch(Exception e)
    {
      System.out.println("Server socket could not be created!");
    }

    System.out.println("FOR REAL");
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
