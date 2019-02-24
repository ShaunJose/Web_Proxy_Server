/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Handles HTTPS requests from the Server side, listens to server and sends to client
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class ServerListenHTTPS
{
  //class variables
  private DataInputStream serverIn;
  private DataOutputStream clientOut;
  private Thread clientThread;

  /**
   * Constructor. initialises class variables (in/out streams) using sockets
   *
   * @param clientSock: Socket through which the client is connected to proxy
   * @param serverSock: Socket through which the proxy is connected to server
   */
  ServerListenHTTPS(Socket clientSock, Socket serverSock, Thread clientThread)
  {
    try
    {
      this.serverIn = new DataInputStream(serverSock.getInputStream());
      this.clientOut = new DataOutputStream(clientSock.getOutputStream());
      this.clientThread = clientThread;
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
  public void listenAndSend()
  {
    //set up array where you storing the bytes
    byte[] messageBytes = new byte[RequestHandler.MAX_BYTES];
    int retVal; // return value from the read function
    boolean serverSending = true;
    int ctr = 1; //iteration counter

    try
    {
      //get bytes from server and send to client
      while(serverSending)
      {
        retVal = serverIn.read(messageBytes); //get message and return value
        serverSending = retVal != -1; //update the serverSending boolean
        //send the client the bytes if there are bytes to send
        if(serverSending)
        {
          clientOut.write(messageBytes, 0, retVal);
          clientOut.flush(); //flush it out
        }
        System.out.println("Server Iteration: " + ctr++); //print iteration
      }

      //wait until client is done
      while(clientThread.isAlive())
      {}
    }

    catch(Exception e)
    {
      e.printStackTrace();
    }

  }
}
