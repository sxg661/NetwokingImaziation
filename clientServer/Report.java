package clientServer;
import java.io.*;


/**
 * Utility class used to write different types to text to the console
 * (I took this from the messaging assignment in first year)
 * @author Martin Escardo 
 *
 */
public class Report {
  
  
 
  /**
  * Sends a notification to the console
  * @param message
  */
  public synchronized static void notify(String message) {
	  System.out.println(message);
  }

  /**
   * Sends a notification 
   * @param message
   */
  public synchronized static void error(String message) {
    System.err.println(message);
  }

 
  /**
   * Sends an error message the console and terminates the thread
   * @param message
   */
  public synchronized static void errorAndGiveUp(String message) {
    Report.error(message);
    System.exit(1);
  }
}
