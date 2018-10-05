package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import clientServer.Report;
import server.ClientTable;
import server.Response;
import server.Response.responseType;
import users.UserTable;


/**
 * Stores and manages all the games.
 * @author sophieguile
 *
 */
public class GameTable {
	
	
	/**
	 * contains all the games the are currently running
	 */
	private static ConcurrentHashMap<Integer, Game> activeGames = new ConcurrentHashMap<Integer,Game>();
	
	
	/**
	 * contains all the games waiting for more players
	 */
	private static ConcurrentHashMap<Integer, Game> deactiveGames = new ConcurrentHashMap<Integer,Game>();
	
	
	/**
	 * contains a queue of the IDs of the games waiting for more players
	 */
    private static List<Integer> waitingGames = Collections.synchronizedList(new ArrayList<Integer>());
    
    //these are for allocating a new unique ID to a game
    
    /**
	 * the next ID to be allocated 
	 */
	private static int nextID = 0; 
	/**
	 * the IDs that have freed up
	 */
	private static ConcurrentLinkedQueue<Integer> freeIDs = new ConcurrentLinkedQueue<Integer>();
	
	
	private final static int[] GAMEDIMENSIONS = new int[]{21,21};
	
	/**
	 * 
	 * @return The dimensions of the map
	 */
	public synchronized static int[] getDimensions(){
		return GAMEDIMENSIONS;
	}
	
	/**
	 * Sends the whole lobby to client as a list of lobby updates.
	 * @param clientID
	 */
	public synchronized static void sendLobby(int clientID){
		for(int gameID : waitingGames){
			Game waitingGame = deactiveGames.get(gameID);
			
			ClientTable.getQueue(clientID).offer(
					new Response("AddGame " + waitingGame.getID()
					+ " " + waitingGame.getNumberOfPlayers() 
					+ " normal", 
					responseType.LOBBYUPDATE));
		}
	}
	
	/**
	 * Sets a client's position to the start position and updates all other players in the game.
	 * @param clientID
	 * @param gameID
	 */
	public synchronized static void sendToStart(int clientID, int gameID){
		if(activeGames.containsKey(gameID))
			activeGames.get(gameID).setToStart(clientID);
	}
	
	
	/**
	 * Gets an unused ID to use for a game.
	 * @return
	 */
	public synchronized static int newID(){
		if(freeIDs.size() > 0){
			return freeIDs.poll();
		}
		else return nextID++;
	}
	
	/**
	 * Adds a bullet to a game.
	 * @param bulletID
	 * @param bulletX
	 * @param bulletY
	 * @param gameID
	 * @param clientID the client who fired the bullet
	 */
	public synchronized static void addBullet(
			int bulletID, int bulletX, int bulletY, int gameID, int clientID){
		if(activeGames.containsKey(gameID)){
			activeGames.get(gameID).addBullet(bulletID, bulletX, bulletY, clientID);
		}
			
		
	}
	
	/**
	 * Updates the positions of the enemies in the game.
	 * @param gameID
	 */
	public synchronized static void updateEnemies(int gameID){
		if(activeGames.containsKey(gameID)){
			activeGames.get(gameID).updateEnemies();
		}
			
	}
	
	
	/**
	 * Sends a response to everyone in a game.
	 * @param response
	 * @param gameID
	 */
	public synchronized static void sendToGame(Response response, Integer gameID){
		if(activeGames.containsKey(gameID))
			activeGames.get(gameID).sendToAll(response);
		else if(deactiveGames.containsKey(gameID))
			deactiveGames.get(gameID).sendToAll(response);
		else
			Report.error("Attempted to send message to non-existant game");
	}
	
	/**
	 * When a game gets enough players this will do all the initializations and lets
	 * all the clients know that the game is starting, as well as sending out the map
	 * to all the clients.
	 * @param game
	 */
	public synchronized static void activateGame(Game game){	
		
		game.initialiseMap();
		
		game.selectEnemyUpdater();
		
		Report.notify("Sending the map");
		game.getMap().display();
		Report.notify("___________________________________");
		//sends the map to all the players
		game.sendToAll(new Response(game.getMap()));
		
		game.sendToAll(new Response(responseType.GAMEACTIVE));
		
		
		waitingGames.removeIf(i -> i == game.getID());
		
		deactiveGames.remove(game.getID());
		activeGames.put(game.getID(), game);
		ClientTable.addToAllQueues(
				new Response("RemoveGame " + game.getID(), responseType.LOBBYUPDATE));
		
		game.initialiseEnemies();
		game.initaliseBulletTable();
	}
	
	
	
	/**
	 * If a game loses players this will let all the clients know that game is 
	 * now waiting for players and the game will be added back to the lobby.
	 * @param game
	 * @param recycle
	 */
	public synchronized static void deactivateGame(Game game, boolean recycle){
		//this will happen if a player leaves a game
		//the recyle boolean is true if this game needs to be put back into the queue
		//upon deactivation, e.g. if a player leaves the game and the other players can wait
		//for a new player
		//game.terminateEnemyThread();
		assert(activeGames.containsKey(game.getID()));
		
		activeGames.remove(game.getID());
		
		game.wipeEnemyInfo();
		
		if(game.getNumberOfPlayers() > 0 && recycle){
			waitingGames.add(game.getID());
			deactiveGames.put(game.getID(), game);
			ClientTable.addToAllQueues(
					new Response("AddGame " + game.getID() 
					+ " " + game.getNumberOfPlayers() 
					+ " normal", 
					responseType.LOBBYUPDATE));
		}
		else if(!recycle){
			ClientTable.addToAllQueues(
					new Response("RemoveGame " + game.getID(),
					responseType.LOBBYUPDATE));
			freeIDs.offer(game.getID());
		}

			
		//otherwise we can just ditch the game
	}
	
	
	/**
	 * Moves a player in a specified direction and sends updates to all the clients.
	 * @param playerID
	 * @param gameID
	 * @param direction
	 */
	public synchronized static void movePlayer(int playerID, int gameID, String direction){
		if(activeGames.containsKey(gameID)){
			Game game = activeGames.get(gameID);
			game.movePlayer(playerID, direction);
			//tells everyone somebody is moving & who is moving
			game.sendToAllButMe(new Response(direction + " " + playerID, responseType.MOVE), playerID);
		}
	}
	
	

	
	public synchronized static void leaveGame(int clientID, int gameID){
		
		if(activeGames.containsKey(gameID)){
			deactivateGame(activeGames.get(gameID), true);
		}
		
		Game game = deactiveGames.get(gameID);
		
		//if the game gets played again, we need a new map to avoid
		//giving anyone an unfair advantage (some players will already
		//have seen the current map)
		
		game.removePlayer(clientID);
		
		game.sendToAll(new Response(responseType.GAMEDEACTIVE));
		game.freeIDs();
		
		ClientTable.getQueue(clientID).offer(
				new Response(responseType.GAMELEFT));
		ClientTable.addToAllQueues(
				new Response("RemovePlayer " + game.getID(), responseType.LOBBYUPDATE));
		game.sendToAllButMe(
				new Response(clientID + "", responseType.PLAYERLEFT), 
				clientID);
		
		//if there are no players left you have to ditch the game
		if(game.getNumberOfPlayers() == 0){
			deactiveGames.remove(gameID);
			waitingGames.removeIf(i -> i == gameID);
			ClientTable.addToAllQueues(
					new Response("RemoveGame " + game.getID(), responseType.LOBBYUPDATE));
		}
		
	}
	
	public synchronized static int startNewGame(int clientID){
		
		Game waitingGame = new Game();
		waitingGame.addPlayer(clientID);
		waitingGames.add(waitingGame.getID());
		
		ClientTable.getQueue(clientID).offer(
				new Response(responseType.GAMEDEACTIVE));
		
		deactiveGames.put(waitingGame.getID(), waitingGame);
		
		ClientTable.addToAllQueues(
				new Response("AddGame " + waitingGame.getID()
				+ " " + waitingGame.getNumberOfPlayers() 
				+ " normal", 
				responseType.LOBBYUPDATE));
		
		return waitingGame.getID();
	}
	
	
	
	public synchronized static boolean joinGame(int clientID, Integer gameID){
		Game waitingGame;
		
		if(deactiveGames.containsKey(gameID)){
			waitingGame = deactiveGames.get(gameID);
			deactiveGames.remove(waitingGame.getID());
			ClientTable.addToAllQueues(
					new Response("AddPlayer " + gameID,responseType.LOBBYUPDATE) );
			
		}
		else{
			return false;
		}
	
		
		waitingGame.sendToAllButMe(new Response(clientID + "", responseType.PLAYERJOINED), clientID);
		
		//tells this player about all the players that are already in the game
		for(int id : waitingGame.getPlayers()){
			ClientTable.getQueue(clientID).offer(new Response(
					id + "", responseType.PLAYERJOINED));
		}
		
		waitingGame.addPlayer(clientID);
		
		if(waitingGame.getNumberOfPlayers() >= 2){
			activateGame(waitingGame);
			activeGames.put(waitingGame.getID(), waitingGame);
		}
		else{
			ClientTable.getQueue(clientID).offer(
					new Response(responseType.GAMEDEACTIVE));
			
			deactiveGames.put(waitingGame.getID(), waitingGame);
			
		}
		
		return true;
	}
	
	public synchronized static void endGame(int winnerID, int gameID){
		
		Game game = activeGames.get(gameID);
		
		game.sendToAllButMe(new Response(responseType.LOSE), winnerID);
		ClientTable.getQueue(winnerID).offer(new Response(responseType.WIN));
		
		disbandGame(gameID);
		
	}
	
	public synchronized static void disbandGame(int gameID){
		Game game = activeGames.get(gameID);
		
		if(activeGames.containsKey(gameID)){
			deactivateGame(activeGames.get(gameID), false);
		}
		
		deactiveGames.remove(game);
		
		game.freeIDs();
		freeIDs.offer(gameID);
		}
	
}
