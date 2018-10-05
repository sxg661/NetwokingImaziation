package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import clientServer.Report;



/**
 * Stores information on all the games currently in the lobby
 * @author sophieguile
 *
 */
public class LobbyInfo {
	private static ConcurrentHashMap<Integer, GameInfo> games = new ConcurrentHashMap<Integer, GameInfo>(); 
	
	private static List<Integer> gameIDs = Collections.synchronizedList(new ArrayList<Integer>());

	
	
	/**
	 * Writes all the game IDs to the array passed in and returns the maximum index of the array
	 * and returns the maximum index at which an ID is stored.
	 * It is done this way to avoid current modification exceptions.
	 * @param IDs the array to write the IDs too
	 * @return the maximum index in the array at which IDs are stored
	 */
	public synchronized static int getGameIDs(Integer[] IDs){
		//return the max index so that the lobby doesn't attempt to read further
		//in the array and get null pointer exceptions
		
		//probably shouldn't have hard coded this :S
		//maybe change later
		int maxIndex = 99;
		
		int i = -1;
		for(int id : gameIDs){
			i++;
			if(i > maxIndex)
				return maxIndex;
			IDs[i] = id;
		}
		
		return i;
	}
	
	
	/**
	 * Returns a GameInfo object for a corresponding ID
	 * @param id the id of the game
	 * @return the corresponding GameInfo object
	 */
	public synchronized static GameInfo getGame(int id){
		return games.get(id);
	}
	
	
	/**
	 * Returns an optional with the number of players in a game.
	 * If the game is no longer in the lobby it will return Optional.empty()
	 * @param ID the id of the game
	 * @return an optional of the number of players in the game, 
	 * empty is the game doesn't exist
	 */
	public synchronized static Optional<Integer> getNumPlayers(int ID){
		if(games.containsKey(ID))
			return Optional.of(games.get(ID).getNumPlayers());
		else return Optional.empty();
	}
	
	
	/**
	 * Returns an optional with the difficulty of a game.
	 * If the game is no longer in the lobby it will return Optional.empty()
	 * @param ID the id of the game
	 * @return optional of the difficulty of the game, 
	 * empty is the game doesn't exist
	 */
	public synchronized static Optional<String> getDifficulty(int ID){
		if(games.containsKey(ID))
			return Optional.of(games.get(ID).getDifficulty());
		else return Optional.empty();
	}

	
	/**
	 * Adds new game information to lobby
	 * @param id the id of the game
	 * @param numberOfPlayers the number of players in the game
	 * @param difficulty the difficulty of the game
	 */
	private synchronized static void addGame(int id, int numberOfPlayers, String difficulty){
		GameInfo newGame = new GameInfo(id, numberOfPlayers, difficulty);
		gameIDs.add(id);
		games.put(id, newGame);
	}
	
	/*
	 * Removes game information from the lobby
	 */
	
	/**
	 * removes game info from the lobby
	 * @param id the id of the game to remove
	 */
	private synchronized static void removeGame(int id){
		gameIDs.removeIf(i -> i == id);
		games.remove(id);
	}
	
	
	
	
	/**
	 * displays the lobby to the console (for testing)
	 */
	public synchronized static void displayLobby(){
		for(int id : gameIDs){
			Report.notify(games.get(id).toString());
		}
	}
	
	
	/**
	 * handles an update to the lobby from the server
	 * @param updateCommand a string the contains the update information
	 */
	public synchronized static void updateLobby(String updateCommand){
		String[] args = updateCommand.split(" ");
		int gameID = Integer.parseInt(args[1]);
		
		switch(args[0]){
		case "RemovePlayer":
			games.get(gameID).removePlayer();
			break;
		case "AddPlayer":
			games.get(gameID).addPlayer();
			break;
		case "AddGame":
			int numPlayers = Integer.parseInt(args[2]);
			addGame(gameID, numPlayers, args[3]);
			break;
		case "RemoveGame":
			removeGame(gameID);
			break;
		default:
			Report.error("invalid lobby update command");
		}
	}
}
