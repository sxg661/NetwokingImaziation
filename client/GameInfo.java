package client;



/**
 * Stores information about a game to be stored in the lobby
 * @author sophieguile
 *
 */
public class GameInfo {
	private int ID;
    private int numPlayers;
    private String difficulty;
    
    public GameInfo(int id, int numPlayers, String difficulty){
    	this.ID = id;
    	this.numPlayers = numPlayers;
    	this.difficulty = difficulty;
    }
    

    
    /**
     * tells the game info it has received an additional player
     */
    public void addPlayer(){
    	numPlayers++;
    }
    
    
    /**
     * tells the game info that a player has left
     */
    public void removePlayer(){
    	numPlayers--;
    }
    
  
    
    /**
     * gets the number of players currently in the game
     * @return the number of players in the game
     */
    public int getNumPlayers(){
    	return numPlayers;
    }
    
    
    /**
     * gets the game difficulty
     * @return the difficulty of the game
     */
    public String getDifficulty(){
    	return difficulty;
    }
    
    
    /**
     * For testing, returns a string representation of this game 
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    * @returns a string representation of the game
     */
    public String toString(){
    	return("Game ID : " + ID 
    			+ ", Number of Players : " 
    			+ numPlayers + ", Difficulty : " 
    			+ difficulty);
    }
	
}
