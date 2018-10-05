package game;

import clientServer.Report;

/**
 * Keeps track of the playerl locations
 * @author sophieguile
 *
 */
public class PlayerInfo {
	private int x;
	private int y;
	private int health;
	private int lives;
	
	public PlayerInfo(){
		
		//this is them most common starting position
		this.x = 20;
		this.y = 0;
		
		//the initial health is 100%
		this.health = 100;
		
		//the initial number of lives is 3
		this.lives = 3;
	}
	
	/**
	 * Sets the player's position to x and y.
	 * @param x
	 * @param y
	 */
	public synchronized void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * updates the players position for a move in a certain direction
	 * @param direction
	 */
	public synchronized void move(String direction){
		switch(direction){
		case "up":
			this.y = y - 2;
			break;
		case "down":
			this.y = y + 2;
			break;
		case "left":
	        this.x = x - 2;
	        break;
		case "right":
			this.x = x + 2;
			break;
	    }
	}
}
