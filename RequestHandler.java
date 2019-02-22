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
  public static final String SUCCESS_STATUS = "HTTP/1.1 200 OK";
  public static final String FAILURE_STATUS = "HTTP/1.1 400 Bad Request";
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

       String requestMessage = getRequestMessage(method);
       System.out.println(requestMessage);

     }
     catch(Exception e)
     {
       System.out.println("Could not connect to actual server :(");
       e.printStackTrace();
     }

     System.out.println("Socket request touchdown! :D");
     String requestLine = "";
   }


   /**
    * Finds out the method and host from the input stream of the clientSocket, for HTTP requests
    *
    * @return: String array of the method and host name
    */
   private String[] getHTTPMethodHost()
   {
     //will contain method and host name
     String[] resulArr = new String[2];

     //get method and hostName from HTTP request
     try
     {
       DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); //open input stream
       String requestLine = inputStream.readLine(); //get first line
       resulArr[0] = requestLine.substring(0, requestLine.indexOf(' ')); //method rcv
       while(!requestLine.substring(0, 6).equals("Host: "))//while not host line
       {
         requestLine = inputStream.readLine(); // go to the next line
       }
       resulArr[1] = requestLine.substring(6); //get the name of the host
     }
     catch(Exception e)
     {
       System.out.println("Could not read input request :(");
       e.printStackTrace();
     }

     return resulArr;
   }


   /**
    * Gets the request message from the client
    */
   private String getRequestMessage(String method)
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
         System.out.println("Test: " + requestLine);
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
