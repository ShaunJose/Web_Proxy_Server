/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RequestHandler implements Runnable
{

  //class variables
  private Socket clientSocket;
  private Socket serverSocket;

  /**
   * Constructor. Creates object and initialises clientSocket
   */
   RequestHandler(Socket clientSocket)
   {
     this.clientSocket = clientSocket;
   }


   /**
    * Handles a request appropriately, by connecting to the right server and getting info from it
    *
    * @return: None
    */
   private void processRequest()
   {
     //Get url and port of request
     String[] requestUrlAndPort = getUrlAndPort();

     //Connect to appropriate server
     connect(requestUrlAndPort[0], Integer.parseInt(requestUrlAndPort[1]));

     System.out.println("Socket request touchdown! :D");
   }


   /**
    * Finds out the url and port from the input stream of the clientSocket
    *
    * @return: String array with first element as url name and second element as port number
    */
   private String[] getUrlAndPort()
   {
     //line of request from the client
     String requestLine = "";

     //get input stream of clientSoc and read request line
     try
     {
       DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
       requestLine = inputStream.readLine();
     }
     catch(Exception e)
     {
       System.out.println("Could not read input request :(");
     }

     //get different parts of the request
     String[] requestParts = requestLine.split(" ");

     //get the url name requested by the client and the port number as well
     String[] urlAndPort = requestParts[1].split(":");

     return urlAndPort;
   }


   /**
    */
   private void connect(String url, int port)
   {

   }


  /**
   * Thread starts from here. This function calls processRequest
   *
   * @return: None
   */
  @Override
  public void run()
  {
    processRequest();
  }

}
