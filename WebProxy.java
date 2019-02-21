/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.ServerSocket;
import java.net.Socket;

public class WebProxy implements Runnable
{
  //class variables
  private int port;

  /**
   * Creates a web proxy object
   *
   * @param port: The port at which you want the WebProxy  server to listen
   */
  WebProxy(int port)
  {
    this.port = port;
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
      handleRequest(serverSocket.accept());
      // DataInputStream dIn = new DataInputStream(socket.getInputStream());
      // System.out.println(dIn.readLine());
      // Thread.sleep(5000);
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
      System.out.println("thats what you get");
    }
  }


  /**
   * //TODO: ADD FUNC DESCR HERE
   */
  private void handleRequest(Socket clientSocket)
  {
    //TODO: HANDLE REQUEST HERE
    System.out.println("Not accepting that yet...");
    RequestHandler reqHandler = new RequestHandler(clientSocket);
  }


   /**
    *
    */
   public void closeServer()
   {
     //CLOSE SERVER HERE
   }


  /**
   * Thread of WebProxy object starts here. Just makes the proxy start listening
   */
  @Override
  public void run()
  {
    start_listening();
  }

}
