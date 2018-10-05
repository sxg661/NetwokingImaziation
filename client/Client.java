package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import UI.Boot;
import clientServer.Port;
import clientServer.Report;


/**
 * The client main, run when the client starts
 * @author sophieguile
 *
 */
public class Client {

	private static ClientInfo clientInfo = new ClientInfo();
	
	/**
	 * Gets the client info object which stores all the information about this client
	 * @return The client info object for this client
	 */
	public static synchronized ClientInfo myInfo(){
		return clientInfo;
	}
	
	public static void main(String args[]){
		// Check correct usage:
	    if (args.length != 1) {
	      Report.errorAndGiveUp("Usage: java Client server-hostname");
	    }

	    // Initialize information:
	    String hostname = args[0];
	    

	    // Open sockets:
	    PrintStream toServer = null;
	    BufferedReader fromServer = null;
	    Socket server = null;
	    Boot ui = null;
	    //Creates a buffer reader with the user
	    BufferedReader userLogin = null;

	    try {
	      System.out.print(1);
	      server = new Socket(hostname, Port.number); // Matches AAAAA in Server.java
	      System.out.print(2);
	      toServer = new PrintStream(server.getOutputStream());
	      System.out.print(3);
	      fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
	      System.out.print(4);
	      userLogin = new BufferedReader(new InputStreamReader(System.in)); 
	      System.out.print(5);
	      ui = new Boot();
	    } 
	    catch (UnknownHostException e) {
	      Report.errorAndGiveUp("Unknown host: " + hostname);
	    }   
	    catch (IOException e) {
	      Report.errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
	      e.printStackTrace();
	    }
	    
	    // Create two client threads of a different nature:
	    ClientSender sender = new ClientSender(toServer);
	    ClientReceiver receiver = new ClientReceiver(fromServer);
	    
	    ui.start();
	    sender.start();
	    receiver.start();
	        
	    Report.notify("hello");
	    // Wait for them to end and close sockets.
	    try {
	    	ui.join();
	    	sender.join();
	        receiver.join();
	        toServer.close();
	        fromServer.close();
	        server.close();
	        }
	    catch (IOException e) {
	    	Report.errorAndGiveUp("Something wrong " + e.getMessage());
	    	}
	    catch (InterruptedException e) {
	    	Report.error("Unexpected interruption " + e.getMessage());
	    	}
	}
	
	
}
