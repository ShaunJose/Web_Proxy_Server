/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;

public class WebProxy
{

  private static final int PORT = 4000;

  /**
   * Open a Socket and start listening on port 4000
   *
   * @return: None
   */
  public static void start_listening()
  {
    try
    {
      ServerSocket serverSocket = new ServerSocket(PORT);
      System.out.println("Listening on port " + PORT + "...");
      Socket socket = serverSocket.accept();
      // DataInputStream dIn = new DataInputStream(socket.getInputStream());
      // System.out.println(dIn.readLine());
      // // Thread.sleep(5000);
      // System.out.println(socket.getLocalSocketAddress());
      // System.out.println(socket.getRemoteSocketAddress());
      // System.out.println(socket.toString());
      // System.out.println(socket.getPort());
      // System.out.println(socket.getInetAddress().getHostName());
      // System.out.println(socket.getInetAddress().getLocalHost());
      // System.out.println(socket.getInetAddress().getHostAddress());
      //System.out.println(socket.localport);
    }
    catch(Exception e)
    {
      System.out.println("thats what you get");
    }
  }

}
