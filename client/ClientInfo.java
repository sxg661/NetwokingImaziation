package client;

import java.util.Optional;

import clientServer.CommandQueue;

/*
 * Stores all information about the client and the client's request queue
 */
public class ClientInfo {
	private boolean loggedin = false;
	private Optional<String> userName = Optional.empty();
	private Optional<Integer> ID = Optional.empty();
	private Optional<Integer> gameID = Optional.empty();
	private clientState state = clientState.NOTINGAME;
	private CommandQueue<Request> requestQueue = new CommandQueue<Request>();
	
	
	
	/**
	 * Tells us whether or not the client is in a game
	 * @author sophieguile
	 *
	 */
	public enum clientState{
		INGAME,
		NOTINGAME
	}
	
	/**
	 * Adds a request object to this clients queue to be sent to the server
	 * @param command to add
	 */
	public void addToQueue(Request command){
		requestQueue.offer(command);
	}
	
	/**
	 * Gets and removes the next command from the command queue
	 * @return the next commans
	 */
	public Request nextCommand(){
		return requestQueue.take();
	}
	
	
	/**
	 * Assigns this client its ID
	 * @param id
	 */
	public synchronized void setID(int id){
		ID = Optional.of(id);
	}
	
	
	
	/**
	 * Changes the client state.
	 * @param state new client state
	 */
	public synchronized void setState(clientState state){
		this.state = state;
	}
	
	
	
	/**
	 * Gets the current state of the client.
	 * @return the current state
	 */
	public synchronized clientState getState(){
		return state;
	}
	

	
	/**
	 * Gets the current ID of the client
	 * @return an optinoal of the client ID
	 */
	public synchronized Optional<Integer> getID(){
		return ID;
	}
	
	
	
	
	/**
	 * Clears the clients login info and sets it to logged out
	 */
	public synchronized void logout(){
		loggedin = false;
		userName = Optional.empty();
	}
	
	
	/**
	 * Sets the client to logged in
	 */
	public synchronized void login(){
		loggedin= true;
	}
	
	
	
	
	/**
	 * Returns whether or not the client is logged in.
	 * @return boolean
	 */
	public synchronized boolean isLoggedin(){
		return loggedin;
	}
	
	
	/**
	 * Assigns the passed in user name to the client.
	 * @param username
	 */
	public synchronized void setUserName(String username){
		userName = Optional.of(username);
	}
	
	
	/**
	 * Gets the username of the client (if it has one), otherwise return "unkown".
	 * @return
	 */
	public synchronized String getUserName(){
		if(userName.isPresent())
			return userName.get();
		else return "unknown";
	}
}
