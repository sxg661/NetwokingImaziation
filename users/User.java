package users;

import java.util.Optional;

public class User {
	private final String username;
	private final String password;
	private boolean loggedIn;
	private Optional<Integer> clientID;
	private Optional<Integer> gameID;
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
		this.loggedIn = false;
	}
	
	public synchronized int getGameID(){
		return gameID.get();
	}
	
	public synchronized void joinGame(int gameID){
		this.gameID = Optional.of(gameID);
	}
	
	public synchronized void leaveGame(){
		this.gameID = Optional.empty();
	}
	
	public synchronized String getUsername() {
		return username;
	}
	
	public synchronized String getPassword() {
		return password;
	}
	
	public synchronized Optional<Integer> getClientID() {
		return clientID;
	}
	
	public synchronized boolean isLoggedIn() {
		return loggedIn;
	}
	
	public synchronized boolean login(int clientID){
		if(loggedIn)
			return false;
		this.clientID = Optional.of(clientID);
		loggedIn = true;
		return true;
	}
	
	public synchronized boolean logout(){
		if(!loggedIn)
			return false;
		this.clientID = Optional.empty();
		loggedIn = false;
		return true;
	}
}
