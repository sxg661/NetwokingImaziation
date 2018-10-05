package client;


import java.io.IOException;
import java.io.PrintStream;

import clientServer.Report;


/**
 * Deals with sending requests to the server
 * @author sophieguile
 *
 */
public class ClientSender extends Thread{
	
	private PrintStream server;
	private boolean quit;
	
	public ClientSender(PrintStream server) {
	    this.server = server;
	  }
	
	public void run() {
		// So that we can use the method readLine:
		
	    while(!quit)  {
	    	try {
				Request command = Client.myInfo().nextCommand();
				
				mainMenu(command);
	    	}
	    	catch (IOException e) {
				System.out.println("Failed to get input from client:\n\n");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    }
	}

	/**
	 * Deals with a command from the Boot (gui) thread an sends the appropriate packets to the server
	 * @param the request for the menu to deal with
	 * @throws IOException
	 */
	private void mainMenu(Request command) throws IOException {
		switch(command.getType()){
		case LOGIN:
			
			String username = command.getText1();
			String password = command.getText2();
			server.println("login");
			server.println(username);
			server.println(password);
			break;
			
		case REGISTER:
			server.println("register");
			server.println(command.getText1());
			server.println(command.getText2());
			server.println(command.getText3());
			break;
		case JOINGAME:
			server.println("joingame");
			server.println(command.getText1());
			break;
		case WAVE:
			server.println("wave");
			break;
		case MOVE:
			server.println("move");
			server.println(command.getText1());
			break;
		case UPDATEENEMIES:
			server.println("updateEnemies");
			break;
		case WAVEGAME:
			server.println("wavegame");
			break;
		case LEAVEGAME:
			server.println("leavegame");
			break;
		case RESTART:
			server.println("restart");
			break;
		case LOGOUT:
			server.println("leavegame");
			server.println("logout");
			break;
		case QUIT:
			server.println("leavegame");
			server.println("logout");
			server.println("quit");
			quit = true;
			break;
		case WIN:
			server.println("win");
			break;
		default:
			Report.error("invalid command " + command);
		}
	}
	
	
	
	

}
	

