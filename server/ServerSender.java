package server;

import java.io.PrintStream;

import clientServer.Report;
import game.GameTable;
import gameLogic.Map;

/**
 * Class that sends responses back to the client.
 * @author sophieguile
 *
 */
public class ServerSender extends Thread{
	private MyClientInfo myclient;
	private PrintStream client;

	public ServerSender(PrintStream client, MyClientInfo myclient) { 
	    this. client = client;
	    this.myclient = myclient;
	  }
	
	public void run(){
		boolean quit = false;
		client.println("id");
		client.println(myclient.myID());
		GameTable.sendLobby(myclient.myID());
		
		while(!quit){
			Response response = ClientTable.getQueue(myclient.myID()).take();
			
			switch(response.getType()){
			case MESSAGE:
				client.println(response);
				break;
			case LOGIN:
				client.println("login");
				client.println(response);
				break;
			case LOGOUT:
				client.println("logout");
				break;
			case GAMEDEACTIVE:
				client.println("deactive");
				break;
			case GAMEACTIVE:
				client.println("active");
				break;
			case GAMELEFT:
				client.println("gameleft");
				break;
			case PLAYERJOINED:
				client.println("playerjoined");
				client.println(response.getText());
				break;
			case NEWBULLET:
				client.println("new bullet");
				String[] bulletInfo = response.getText().split(" ");
				//sends the id of the bullet
				client.println(bulletInfo[0]);
				//sends the x of the bullet
				client.println(bulletInfo[1]);
				//sends the y of the bullet
				client.println(bulletInfo[2]);
				break;
			case ENEMYJOINED:
				client.println("enemyjoined");
				String[] info = response.getText().split(" ");
				//sends the id of the enemy
				client.println(info[0]);
				//sends the x position
				client.println(info[1]);
				//sends the y position
				client.println(info[2]);
				break;
			case ENEMYUPDATER:
				client.println("enemyupdater");
				break;
			case PLAYERLEFT:
				client.println("playerleft");
				client.println(response.getText());
				break;
			case LOSE:
				client.println("lose");
				myclient.clearGameID();
				break;
			case WIN:
				client.println("win");
				myclient.clearGameID();
				break;
			case MOVE:
				client.println("move");
				//moves in format "<direction> <playerID>" e.g. left 7.
				String[] moveInfo = response.getText().split(" ");
				//sends the direction
				client.println(moveInfo[0]);
				//send the player id
				client.println(moveInfo[1]);
				break;
			case MOVENEMY:
				client.println("movenemy");
				//moves in format "<direction> <playerID>" e.g. left 7.
				String[] enemyMoveInfo = response.getText().split(" ");
				//sends the direction
				client.println(enemyMoveInfo[0]);
				//send the player id
				client.println(enemyMoveInfo[1]);
				break;
			case TOSTART:
				client.println("tostart");
				client.println(response.getText());
				break;
			case SENDMAP:
				client.println("map");
				Report.notify("sending the map");
				Integer[] dimensions = response.getMap().getMapDimension();
				client.println(dimensions[0]);
				client.println(dimensions[1]);
				//the map is picked up by an identical set of for loops on the
				//other side
				for(int i = 0; i < dimensions[0]; i++){
					for(int j = 0; j< dimensions[1]; j++){
						client.println(response.getMap().getOneChar(i, j));
					}
				}
				break;
			case LOBBYUPDATE:
				client.println("lobbyUpdate");
				client.println(response.getText());
				break;
			case QUIT:
				client.println("quit");
				ClientTable.removeClient(myclient.myID());
				quit = true;
			    break;
			}
	 }
	}
	
	
}
