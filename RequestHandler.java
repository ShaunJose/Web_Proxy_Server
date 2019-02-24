/* author: Shaun Jose
   github: github.com/ShaunJose
   Class Description: Handles all types of requests appropriately (http or https)
*/

//imports
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class RequestHandler implements Runnable
{
  //define type enum
  enum ReqType { HTTP, HTTPS; }

  //class variables
  private Socket clientSocket;
  private Socket serverSocket;
  private ReqType type;
  private int port;

  //constants
  public static final String SUCCESS_STATUS = "HTTP/1.1 200 Connection established\n\r\n";
  public static final String FORBIDDEN_STATUS = "HTTP/1.1 403 Access forbidden\r\n\r\n";
  public static final int HTTP_PORT = 80;
  public static final int MAX_BYTES = 4096;

  /**
   * Constructor. Creates object and initialises clientSocket, type to HTTP (i.e. not secure) by default and port set to HTTP_PORT by default as well
   *
   * @param clientSocket: The socket through which the client sent a request
   */
   RequestHandler(Socket clientSocket)
   {
     this.clientSocket = clientSocket;
     this.type = ReqType.HTTP;
     this.port = HTTP_PORT;
   }


   /**
    * Handles a request appropriately, by connecting to the right server and getting info from it
    *
    * @return: None
    */
   private void processRequest()
   {
     //Get method, host, and full request. Also change port and type depending on if the request is an https or http one
     String[] reqStuff = getMethodHostReq();
     String method = reqStuff[0];
     String hostName = reqStuff[1];
     String request = reqStuff[2];
     System.out.println("New " + type + " request:\n" + request);

     //check if host is blocked
     if(ManagementConsole.blocked(hostName))
     {
       try { //send FORBIDDEN_STATUS to client
         DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
         outputStream.writeUTF(RequestHandler.FORBIDDEN_STATUS);
         outputStream.flush();
       }
       catch(Exception e) { e.printStackTrace(); }
       System.out.println("Client with address " + clientSocket.getRemoteSocketAddress() + " tried to access " + hostName + "!"); //let manager know who tried to access what
       this.shutDown(); //close client socket connection
       return; //end request thread
     }

     //check if it's cached (only http can be cached)
     String response = null;
     if(type == ReqType.HTTP && ManagementConsole.isCached(hostName))
     {
       response = ManagementConsole.getFromCache(hostName);
       System.out.println("Cache Hit!\n");
     }

     //Connect to appropriate server and send status message to client
     try
     {
       //open connection to server
       serverSocket = new Socket(hostName, this.port);

       //send client HTTP request to server if not retrieved from cache
       DataOutputStream outputStream;
       if(type == ReqType.HTTP && response == null)
       {
         outputStream = new DataOutputStream(this.serverSocket.getOutputStream());
         outputStream.writeUTF(request);
         outputStream.flush();
       }

       // set up client output stream
       outputStream = new DataOutputStream(clientSocket.getOutputStream());

       //if req is https, listen to client and server until they're both done
       if(type == ReqType.HTTPS)
       {
         //send success message to client!
         outputStream = new DataOutputStream(clientSocket.getOutputStream());
         outputStream.writeBytes(RequestHandler.SUCCESS_STATUS);
         outputStream.flush();

         //listen to client on another thread
         ClientListenHTTPS clientListener = new ClientListenHTTPS(clientSocket, serverSocket);
         Thread clientThread = new Thread(clientListener);
         clientThread.start();
         //listen to server using the current thread
         ServerListenHTTPS serverListener = new ServerListenHTTPS(clientSocket, serverSocket, clientThread);

         serverListener.listenAndSend();
       }
       else //if req is http, then send response once and relax :)
       {
         if(response == null) //if not retrieved from cache
           response = getHTTPResponse();//get response from server in response to query

         //save response to cache or update it's position for LRU policy
         ManagementConsole.saveToCache(hostName, response);

         //send response to client
         outputStream.writeUTF(response);
         outputStream.flush();
       }

     }

     catch(Exception e)
     {
       System.out.println("Could not connect to actual server :( \n" +
       "Check if you entered the url correctly!!");
       e.printStackTrace();
     }

     System.out.println("Socket request touchdown! :D");

     this.shutDown();
   }


   /**
    * Finds out the method, host and request from the input stream of the clientSocket, for HTTP/HTTPS requests. Also manages class variables depending on the type of request (http/s)
    *
    * @return: String array of the method, host name and the request
    */
   private String[] getMethodHostReq()
   {
     String[] reqStuff = new String[3]; //will contain method, host name, req
     String requestMessage = ""; //will contain entire request message

     //get method and hostName from HTTP request
     try
     {
       DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); //open input stream

       //get method
       requestMessage = inputStream.readLine() + "\r\n"; //get first line of req
       int firstSpace = requestMessage.indexOf(' ');
       reqStuff[0] = requestMessage.substring(0, firstSpace);

       //get rest of message and hostName
       String restOfMessage = getHTTPRequest(reqStuff[0]);
       reqStuff[1] = restOfMessage.substring(6, restOfMessage.indexOf("\r\n"));

       if(reqStuff[0].equals("CONNECT"))
        reqStuff[1] = requestMessage.substring(8, requestMessage.lastIndexOf(' '));
       requestMessage += restOfMessage; //update request message
     }

     catch(Exception e)
     {
       System.out.println("Could not read input request :(");
       e.printStackTrace();
     }

     //change variables if it's an https request, by checking what method is
     if(reqStuff[0].equals("CONNECT"))
     {
       String[] hostAndPort = reqStuff[1].split(":"); //split host and port
       reqStuff[1] = hostAndPort[0]; //first part is host
       System.out.println(requestMessage);
       this.port = Integer.parseInt(hostAndPort[1]); //second part is port num
       this.type = ReqType.HTTPS; //type is an HTTPS request
     }

     reqStuff[2] = requestMessage;

     return reqStuff;
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
    * Closes the client and server socket connections
    */
   private void shutDown()
   {
     try
     {
       if(clientSocket != null && !clientSocket.isClosed())
         clientSocket.close();

       if(serverSocket != null && !serverSocket.isClosed())
         serverSocket.close();
     }

     catch(Exception e)
     {
       e.printStackTrace();
     }
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
