/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Handles HTTPS requests from the Client side, listens to client and sends to server
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class ClientListenHTTPS implements Runnable
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
      //initialise IO streams using the sockets passed
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
  private void listenAndSend()
  {
    //set up array where you storing the bytes
    byte[] messageBytes = new byte[RequestHandler.MAX_BYTES];
    int retVal; // return value from the read function
    boolean clientSending = true;

    try
    {
      //get bytes from client and send to server
      while(clientSending)
      {
        retVal = clientIn.read(messageBytes); //get message and return value
        clientSending = retVal != -1; //update the clientSending boolean
        //send the server the bytes if there are bytes to send
        if(clientSending)
        {
          serverOut.write(messageBytes, 0, retVal);
          serverOut.flush(); //flush it out
        }
      }
    }

    catch(Exception e)
    {
      e.printStackTrace();
    }

  }


  /**
   * Thread starts from here. This function calls listenAndSend
   *
   * @return: None
   */
  @Override
  public void run()
  {
    listenAndSend();
  }

}
