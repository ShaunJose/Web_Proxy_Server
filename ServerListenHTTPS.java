/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Handles HTTPS requests from the Server side, listens to server and sends to client
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class ServerListenHTTPS implements Runnable
{
  //class variables
  private DataInputStream serverIn;
  private DataOutputStream clientOut;

  /**
   * Constructor. initialises class variables (in/out streams) using sockets
   *
   * @param clientSock: Socket through which the client is connected to proxy
   * @param serverSock: Socket through which the proxy is connected to server
   */
  ServerListenHTTPS(Socket clientSock, Socket serverSock)
  {
    try
    {
      this.serverIn = new DataInputStream(serverSock.getInputStream());
      this.clientOut = new DataOutputStream(clientSock.getOutputStream());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }


  /**
   * Listens to the server and sends to the client until server is done
   *
   * @return: None
   */
  private void listenAndSend()
  {
    System.out.println("Hi from le server!");
  }

  /**
   * Thread starts from here. This function calls processRequest
   *
   * @return: None
   */
  @Override
  public void run()
  {
    listenAndSend();
  }
}
