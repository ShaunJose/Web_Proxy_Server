/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Handles HTTPS requests from the Client side, listens to client and sends to server
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class ClientListenHTTPS
{
  //class variables
  private DataInputStream clientIn;
  private DataOutputStream serverOut;

  /**
   * Constructor. initialises class variables (in/out streams) using sockets
   *
   * @param clientSock: Socket through which the client is connected to proxy
   * @param serverSock: Socket through which the proxy is connected to server
   */
  ClientListenHTTPS(Socket clientSock, Socket serverSock)
  {
    try
    {
      this.clientIn = new DataInputStream(clientSock.getInputStream());
      this.serverOut = new DataOutputStream(serverSock.getOutputStream());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }


  /**
   * Listens to the client and sends to the server until client is done
   *
   * @return: None
   */
  public void listenAndSend()
  {
    System.out.println("Hi from le client-o!");
  }

}
