package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import clientServer.Port;
import clientServer.Report;
import game.GameTable;
import users.UserTable;

public class Server {

	public static void main(String args[]){
		ServerSocket serverSocket = null;
		
		UserTable.readInFile();
	    
	    try {
	      serverSocket = new ServerSocket(Port.number);
	    } 
	    catch (IOException e) {
	      Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
	    }
	    
	    try{ 
	    	
	        while (true) {
	          // Listen to the socket, accepting connections from new clients:
	          Socket socket = serverSocket.accept();
	          
	          BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	          
	          PrintStream toClient = new PrintStream(socket.getOutputStream());
	          
	          
	          MyClientInfo myclient = new MyClientInfo(ClientTable.newClientID());
	          
	          
	          Report.notify("Client " + myclient.myID() + " has connected");
	          
	          // We create and start a new thread to read from the client:
	          (new ServerReceiver(fromClient, myclient)).start();

	          // We create and start a new thread to write to the client:
	          (new ServerSender(toClient, myclient)).start();
	          }
	    }
	       
	     catch(IOException e) {
	         Report.error("IO error " + e.getMessage());
	    }	
	    
	        
	}
}
