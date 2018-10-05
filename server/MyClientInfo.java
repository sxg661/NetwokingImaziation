package server;

import java.util.Optional;

public class MyClientInfo {
	
	private int clientID;
	
	private Optional<String> userName;
	private Optional<Integer> gameID;
	
	public MyClientInfo(Integer clientID){
		this.clientID = clientID;
		userName = Optional.empty();
		gameID = Optional.empty();
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer myID(){
		return clientID;
	}
	
	public void clearUserName(){
		userName = Optional.empty();
	}
	
	public void setUserName(String userName){
		this.userName = Optional.of(userName);
	}
	
	public boolean isLoggedIn(){
		return userName.isPresent();
	}
	
	public String myUsername(){
		return userName.get();
	}
	
	public void clearGameID(){
		gameID = Optional.empty();
	}
	
	public void setGameID(Integer clientID){
		this.gameID = Optional.of(clientID);
	}
	
	public boolean isInGame(){
		return gameID.isPresent();
	}
	
	public Integer myGameID(){
		return gameID.get();
	}
	
	
	
	
}
