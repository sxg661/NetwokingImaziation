package server;

import java.util.List;
import java.util.Optional;

import game.Game;
import gameLogic.Map;

/**
 * Stored all the information about a response to be sent back to the client
 * @author sophieguile
 *
 */
public class Response {

  private Optional<String> text = Optional.empty();
  
  private Optional<Map> map = Optional.empty();
  
  private Optional<List<Game>> games = Optional.empty();
  
  public Map getMap(){
	  return map.get();
  }

  public enum responseType {
	  //just sends text to the client
	  MESSAGE,
	  //tells client they're logged in
	  LOGIN,
	  //tells client they've been logged out
	  LOGOUT,
	  //tell client they have left the game
	  GAMELEFT,
	  //tells client that their game is starting
	  GAMEACTIVE,
	  //tells client that their game is waiting for players
	  GAMEDEACTIVE,
	  //sends the game map to client
	  SENDMAP,
	  //tells client a player has joined the game they're in
	  PLAYERJOINED,
	  //tells the client that an enemy has joined the game
	  ENEMYJOINED,
	  //tells the client that a player has left the game they're in
	  PLAYERLEFT,
	  //tells the client that a player has moved
	  MOVE,
	  //tells the client that a player has moved back to the start
	  TOSTART,
	  //tells the client that an enemy has moved
	  MOVENEMY,
	  //tells the client that they have won the game
	  WIN,
	  //tells the client they have lost the game
	  LOSE,
	  //tells the client that a new bullet has been fired
	  NEWBULLET,
	  //tells the client that the lobby has changed and sends the update
	  LOBBYUPDATE,
	  //enemy updater will send a response to a client telling them
	  //they need to send a request to update enemies every time they render
	  ENEMYUPDATER,
	  //tells the client that they have quit
	  QUIT
  }
  
  private final responseType type;
  
  public responseType getType(){
	  return type;
  }
  
  public Response(String text) {
	  this.text = Optional.of(text);
	  this.type = responseType.MESSAGE;
  }
  
  public Response(String text, responseType type) {
	  this.text = Optional.of(text);
	  this.type = type;
  }
  
  public Response(Map map) {
	  this.map = Optional.of(map);
	  this.type = responseType.SENDMAP;
  }
  
  public Response(responseType type){
	  this.type = type;
  }
  
  

  public String getText() {
	  assert(text.isPresent());
	  return text.get();
  }
  
  public String toString(){
	  assert(text.isPresent());
	  return text.get();
  }
  
}
