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
   *
   */
  @Override
  public void run()
  {

  }
  
}
