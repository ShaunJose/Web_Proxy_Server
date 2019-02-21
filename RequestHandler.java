/* author: Shaun Jose
   github: github.com/ShaunJose
*/

//imports
import java.net.Socket;

class RequestHandler implements Runnable
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
     //TODO: PROCESS THE REQUEST HERE
     System.out.println("Crazy one: " + this.socket.toString());
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
