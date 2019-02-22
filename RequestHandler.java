/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class RequestHandler implements Runnable
{

  //class variables
  private Socket clientSocket;
  private Socket serverSocket;

  //constants
  public static final String SUCCESS_STATUS = "HTTP/1.1 200 Connection established\r\n\r\n";
  public static final String FAILURE_STATUS = "HTTP/1.1 400 Bad request";
  public static final int HTTP_PORT = 80;

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
     //Get url name of request, i.e. url client wants to connect to
     String[] methodAndHost = getHTTPMethodHost();
     String method = methodAndHost[0];
     String hostName = methodAndHost[1];

     //Connect to appropriate server and send status message to client
     try
     {
       //open connection to server
       serverSocket = new Socket(hostName, RequestHandler.HTTP_PORT);

       //send status to client via clientSocket
       DataOutputStream outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
       outputStream.writeBytes(RequestHandler.SUCCESS_STATUS);
       outputStream.flush();
     }

     catch(Exception e)
     {
       System.out.println("Could not connect to actual server :(");
       e.printStackTrace();
     }

     System.out.println("Socket request touchdown! :D");
   }


   /**
    * Finds out the method and host from the input stream of the clientSocket, for HTTP requests. Also prints out the entire request message
    *
    * @return: String array of the method and host name
    */
   private String[] getHTTPMethodHost()
   {
     String[] resulArr = new String[2]; //will contain method and host name
     String requestMessage = ""; //will contain entire request message

     //get method and hostName from HTTP request
     try
     {
       DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); //open input stream

       //get method
       requestMessage = inputStream.readLine() + "\r\n"; //get first line of req
       resulArr[0] = requestMessage.substring(0, requestMessage.indexOf(' '));

       //get rest of message
       String restOfMessage = getHTTPRequest(resulArr[0]);
       resulArr[1] = restOfMessage.substring(6, restOfMessage.indexOf("\r\n")); //get the name of the host
       requestMessage += restOfMessage; //update request message
     }

     catch(Exception e)
     {
       System.out.println("Could not read input request :(");
       e.printStackTrace();
     }

     System.out.println("New request: \n" + requestMessage); //print request as proof that it works

     return resulArr;
   }


   /**
    * Gets the request message from the client
    *
    * @param method: HTTP method being used
    *
    * @return: String with the HTTP request from the client side
    */
   private String getHTTPRequest(String method)
   {
     String requestLine = "";
     String requestMessage = "";
     Scanner sc = null;

     //get input stream
     try
     { sc = new Scanner(clientSocket.getInputStream()); }
     catch(Exception e)
     { e.printStackTrace(); }

     //get header of client message
     do
     {
       requestLine = sc.nextLine() + "\r\n";
       requestMessage += requestLine;
     } while(!requestLine.equals("\r\n"));

     //get body if it exists
     if(method.equals("POST") || method.equals("PUT"))
     {
       requestLine = "";
       String body = "";

       //get body of client message
       while(sc.hasNext())
       {
         requestLine = sc.nextLine() + "\r\n";
         requestMessage += requestLine;
       }
     }

     return requestMessage;
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
