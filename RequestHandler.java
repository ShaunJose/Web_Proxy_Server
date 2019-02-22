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
     String[] reqStuff = getHTTPMethodHostReq();
     System.out.println("New request:\n" + reqStuff[2]);
     String method = reqStuff[0];
     String hostName = reqStuff[1];
     boolean blocked = false;

     if(ManagementConsole.blocked(hostName))
     {
       System.out.println("Client with address " + clientSocket.getRemoteSocketAddress() + " tried to access " + hostName + "!");
       try
       {
         this.clientSocket.close();
       }
       catch(Exception e)
       {
         e.printStackTrace();
       }
       return;
       //TODO: close server and thread here
     }

     //Connect to appropriate server and send status message to client
     try
     {
       //open connection to server
       serverSocket = new Socket(hostName, RequestHandler.HTTP_PORT);

       //send client request to server
       DataOutputStream outputStream;
       outputStream = new DataOutputStream(this.serverSocket.getOutputStream());
       outputStream.writeBytes(reqStuff[2]);
       outputStream.flush();

       //get response from server in response to query
       String response = getHTTPResponse();
       //send response to client
       outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
       outputStream.writeUTF(response);
       outputStream.flush();
     }

     catch(Exception e)
     {
       System.out.println("Could not connect to actual server :( \n" +
       "Check if you entered the url correctly!!");
       e.printStackTrace();
     }

     System.out.println("Socket request touchdown! :D");
   }


   /**
    * Finds out the method, host and request from the input stream of the clientSocket, for HTTP requests.
    *
    * @return: String array of the method, host name and the request
    */
   private String[] getHTTPMethodHostReq()
   {
     String[] resulArr = new String[3]; //will contain method, host name, req
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

     resulArr[2] = requestMessage;

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

     //get client input stream
     try
     { sc = new Scanner(clientSocket.getInputStream()); }
     catch(Exception e)
     { e.printStackTrace(); }

     //get header of client request
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

       //get body of client request
       do
       {
         requestLine = sc.nextLine() + "\r\n";
         requestMessage += requestLine;
       } while(!requestLine.equals("</html>") && sc.hasNext());
     }

     return requestMessage;
   }


   /**
    * Gets the response message from the server
    *
    * @return: String with the HTTP request from the client side
    */
   private String getHTTPResponse()
   {
     String responseLine = "";
     String responseMessage = "";
     Scanner sc = null;

     //get server input stream
     try
     { sc = new Scanner(serverSocket.getInputStream()); }
     catch(Exception e)
     { e.printStackTrace(); }

     //get header of server response
     do
     {
       responseLine = sc.nextLine() + "\r\n";
       responseMessage += responseLine;
     } while(!responseLine.equals("\r\n"));

     //get body
     responseLine = "";
     String body = "";

     //get body of server response
     do
     {
       responseLine = sc.nextLine() + "\r\n";
       responseMessage += responseLine;
     } while(!responseLine.contains("</html>") && sc.hasNext());

     return responseMessage;
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
