package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import Physics.BulletTable;
import Physics.EnemyTable;
import clientServer.Report;
import gameLogic.Map;
import mazeCreation.MazeGenerator;
import server.ClientTable;
import server.Response;
import server.Response.responseType;
import users.UserTable;



/**
 * Stored all the game information on the server
 * @author Sophie Guile
 *
 */
public class Game{
	private Optional<EnemyTable> enemyTable = Optional.empty();
	private Optional<BulletTable> bulletTable = Optional.empty();
	private List<Integer> playerIds = Collections.synchronizedList(new ArrayList<Integer>());
	private ConcurrentHashMap<Integer,PlayerInfo> players = new ConcurrentHashMap<Integer,PlayerInfo>();
	private Map map;
	private int gameID;
	private int[] start = new int[]{0,640};
	
	private int[] GAMEDIMENSIONS = GameTable.getDimensions();
	
	/**
	 * Makes a new game.
	 */
	public Game(){
		map = new Map();
		initialiseMap();
		//the game table will allocate an id for this game
		gameID = GameTable.newID();
		start[0] = findXStart();
	}
	
	
	
	/**
	 * Sets a players position to the start point and sends its new position to all
	 * the other clients in the game
	 * @param id
	 */
	public void setToStart(int id) {
		players.get(id).setPosition(start[0], start[1]);
		sendToAllButMe(new Response(id + "",responseType.TOSTART), id);
	}
	
	
	/**
	 * Finds the x position of the start square in a gird
	 * (the y position is always 20)
	 * @return x position to render the player at when they start (not x tile)
	 */
	public int findXStart() {
		int startX = 0; 
		for (int i=0; i<21; i++) {
			if (map.getMap()[20][i] == 'S'){
				startX = i;
				break;
			}
		}
		return startX * 32;
	}

	
	/**
	 * Clears the enemy table
	 */
	public synchronized void wipeEnemyInfo(){
		enemyTable = Optional.empty();
	}
	
	
	/**
	 * Assigns one of the players in the game the responsibility of triggering the enemy update.
	 */
	public synchronized void selectEnemyUpdater(){
		ClientTable.getQueue(playerIds.get(0)).offer(
				new Response(responseType.ENEMYUPDATER));
	}
	
	/**
	 * Initializes the locations of the enemies and sends the enemy information to the clients.
	 */
	public synchronized void initialiseEnemies(){
		enemyTable = Optional.of(new EnemyTable(this));
		map.display();
		enemyTable.get().initialiseEnemies(map.getMap().clone());
	}
	
	/**
	 * Initializes the table to store the bullets.
	 */
	public synchronized void initaliseBulletTable(){
		bulletTable = Optional.of(new BulletTable());
	}
	
	/**
	 * Frees all the IDs occupied by the enemies from the client table
	 */
	public synchronized void freeIDs(){
		if(enemyTable.isPresent())
			enemyTable.get().freeIDs();
	}
	
	
	/**
	 * Updates the enemy locations and sends the updates to the clients.
	 */
	public synchronized void updateEnemies(){
		enemyTable.get().updateEnemies();
	}
	
	
	/**
	 * Generates a new random map.
	 */
	public synchronized void initialiseMap(){
		try {
			Map map = new Map();
			map.getRandomMap(GAMEDIMENSIONS[0], GAMEDIMENSIONS[1]);
		} 
		catch (InterruptedException e) {
			Report.error("Interrupted while resetting maze");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a response to everyone in the game.
	 * @param response
	 */
	public synchronized void sendToAll(Response response){
		for(int clientID : getPlayers()){
			//it is possible for the client to have logged out
			//if they have the game will become deactive soon after this
			if(ClientTable.isValidID(clientID)){
				ClientTable.getQueue(clientID).offer(response);;
			}
		}
	}
	
	/**
	 * Sends a response to everyone in the game except for one player.
	 * @param response
	 * @param myID the id of the player not to send the response to
	 */
	public synchronized void sendToAllButMe(Response response, int myID){
		for(int clientID : getPlayers()){
			//it is possible for the client to have logged out
			//if they have the game will become deactive soon after this
			if(ClientTable.isValidID(clientID) && clientID != myID){
				ClientTable.getQueue(clientID).offer(response);;
			}
		}
	}
	
	/**
	 * 
	 * @return the number of players in the game
	 */
	public synchronized int getNumberOfPlayers(){
		return playerIds.size();
	}
	
	/**
	 * A list of the players in the game
	 * @return
	 */
	public synchronized List<Integer> getPlayers(){
		return playerIds;
	}
	
	/**
	 * Adds a bullet to the game and lets all the player know.
	 * @param bulletID
	 * @param x (not tile)
	 * @param y (not tile)
	 * @param clientID the client who fired the bullet
	 */
	public synchronized void addBullet(int bulletID, int x, int y, int clientID){
		if(bulletTable.isPresent()){
			bulletTable.get().initialiseBullet(bulletID, x, y);
			sendToAllButMe(new Response(
					bulletID + " " + x + " " + y, responseType.NEWBULLET),
					clientID);
		}
	}
	
	/**
	 * Adds a player to the game.
	 * @param clientID
	 */
	public synchronized void addPlayer(int clientID){
		playerIds.add(clientID);
		players.put(clientID, new PlayerInfo());
		players.get(clientID).setPosition(start[0], start[1]);
	}
	
	/**
	 * Removes a player from the game.
	 * @param clientID
	 */
	public synchronized void removePlayer(int clientID){
		players.remove(clientID);
		for (int i = 0; i < playerIds.size() ; i++){
			if (playerIds.get(i) == clientID){
				playerIds.remove(i);
			}
		}
	}
	

	
	public synchronized void movePlayer(int playerID, String direction){
		players.get(playerID).move(direction);
	}
	
	public synchronized int getID(){
		return gameID;
	}
	
	public Map getMap(){
		return map;
	}


	
	
}
