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
  private Socket socket;

  /**
   * Constructor. Creates object and initialises socket
   */
   RequestHandler(Socket socket)
   {
     this.socket = socket;
   }


   /**
    * Handles a request appropriately
    */
   private void processRequest()
   {
     //Get url and port of request
     String[] requestUrlAndPort = getUrlAndPort();

     System.out.println(requestUrlAndPort[0]);
     System.out.println(requestUrlAndPort[1]);

     //Connect to appropriate server
   }


   /**
    *
    */
   private String[] getUrlAndPort()
   {
     //line of request from the client
     String requestLine = "";

     //get input stream of socket and read request line
     try
     {
       DataInputStream inputStream = new DataInputStream(socket.getInputStream());
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
