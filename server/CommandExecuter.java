package server;

import java.util.Optional;

import client.Client;
import clientServer.Report;
import game.GameTable;
import gameLogic.Map;
import server.Response.responseType;
import users.UserTable;


/**
 * An extension of serverReciever that deals with more complex requests.
 * @author sophieguile
 *
 */
public class CommandExecuter {
	private MyClientInfo myclient;
	
	
	public CommandExecuter(MyClientInfo myclient){
		this.myclient = myclient;
	}
	
	/**
	 * Attempts to log in a client, sends back an error message if they fail, otherwise
	 * sends a response telling the client they have logged in.
	 * @param name
	 * @param password
	 */
	public void login(String name, String password){
		String respString = "";
		
		if(myclient.isLoggedIn())
			respString = "You are already logged in as " + 
		        myclient.myUsername();
		else if(!UserTable.isUser(name))
			respString = "This user does not exist";
		else if(!UserTable.authenticate(name, password))
			respString = "Incorrect Passowrd";
		else if(!UserTable.logInUser(name, myclient.myID()))
			respString = "This user is already logged in";
		else{
			respString = "You have logged in as " + name;
			myclient.setUserName(name);
			ClientTable.getQueue(myclient.myID()).offer(new Response(name, responseType.LOGIN));
			Report.notify(name + " has just logged in on client " + myclient.myID());
		}
		
		ClientTable.getQueue(myclient.myID()).offer(new Response(respString));
	}
	
	
	
	/**
	 * Attempts to join the client into an already exisitn game.
	 * @param gameID
	 */
	public void joinGame(Optional<Integer> gameID){
		
		if(myclient.isInGame()){
			ClientTable.getQueue(myclient.myID()).offer( new Response(
					"You are already in a game"));
		}
		else{
			
			if(!gameID.isPresent()){
				int gid = GameTable.startNewGame(myclient.myID());
				ClientTable.getQueue(myclient.myID()).offer(new Response("You have joined a game"));
				myclient.setGameID(gid);
			}
			else if(GameTable.joinGame(myclient.myID(), gameID.get())){
				myclient.setGameID(gameID.get());
				ClientTable.getQueue(myclient.myID()).offer(new Response("You have joined a game"));
			}
			else{
				ClientTable.getQueue(myclient.myID()).offer( new Response(
						"Game has already started or does not exist"));
				//return;
			}
			
			if(myclient.isInGame())
				Report.notify("Player has joined game " + 
					    myclient.myGameID() + " on client " + myclient.myID());
		}
	}
	
	
	
	/**
	 * If the client is in a game it will attempt to make that client leave the game.
	 */
	public void leaveGame(){
		
		if(!myclient.isInGame()){
			ClientTable.getQueue(myclient.myID()).offer( new Response(
					"You are not in a game"));
		}
		else{
			GameTable.sendToGame(new Response(
					"Player has left the game!"), 
					myclient.myGameID());
			GameTable.leaveGame(myclient.myID() , myclient.myGameID());
			
			Report.notify("Player has left the game " + myclient.myGameID() + " on client " + myclient.myID());
			
			myclient.clearGameID();
		}
	}
	
	/**
	 * Moves the client in a given direction if they're in  a game.
	 * @param direction
	 */
	public void move(String direction){
		if(!myclient.isInGame())
			Report.notify("Move from client not in game");
		else{
			GameTable.movePlayer(myclient.myID(), myclient.myGameID() , direction);
		}
	}
	
	public void win(){
		if(!myclient.isInGame()){
			Report.error("Invalid win command");
		}
		else{
			GameTable.endGame(myclient.myID(), myclient.myGameID());
			myclient.clearGameID();
		}
		
	}
	

	
	public void register(String username, String pass1, String pass2){
		String respString = "";
		
		if(myclient.isLoggedIn()){
			respString = "You are already logged in as " + 
			        myclient.myUsername();
		}
		else if(UserTable.isUser(username)){
			respString = "This username is already taken";
		}
		else if(!pass1.equals(pass2)){
			respString = "Passwords did not match";
		}
		else{
			UserTable.createUser(username, pass1);
			UserTable.writeToFile(username, pass1);
			
			Report.notify("User " + username + " has been registered on client " + myclient.myID());
			login(username, pass1);
			respString = "You have registered as " + username;
		}
		
		ClientTable.getQueue(myclient.myID()).offer(new Response(respString));
	}
	
	
	
	
	public void logout(){
		String respString;
		if(!myclient.isLoggedIn())
			respString = "You are not logged in!";
		else{
			respString = "You logged out as " + myclient.myUsername();
			Report.notify(myclient.myUsername() + " has just logged out on client " + myclient.myID());
			UserTable.logoutUser(myclient.myUsername());
			myclient.clearUserName();
			ClientTable.getQueue(myclient.myID()).offer(new Response(responseType.LOGOUT));
		}
		
		ClientTable.getQueue(myclient.myID()).offer(new Response(respString));
	}
}













