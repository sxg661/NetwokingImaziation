package client;

import java.io.BufferedReader;
import java.io.IOException;

import UI.GameManager;
import UI.Lobby;
import UI.GameManager.GameState;
import client.ClientInfo.clientState;
import clientServer.Report;
import rendering.PlayerTable;

public class ClientReceiver extends Thread {
	
	private BufferedReader server;
	
	public ClientReceiver(BufferedReader server) {
	    this.server = server;
	}
    
    public void run(){
    	boolean quit = false;
    	
    	while(!quit){
    		String response;
			try {
				response = server.readLine();
				switch(response){
				case "quit":
					quit = true;
					Report.notify("Goodbye!");
					break;
				case "login":
					Client.myInfo().login();
					Client.myInfo().setUserName(server.readLine());
					GameManager.setStates(GameManager.GameState.EDITOR);
					break;
				case "logout":
					Client.myInfo().logout();
					break;
				case "deactive":
					
					Report.notify("waiting for players");
					if(Client.myInfo().getState() == clientState.INGAME){
						GameManager.setStates(GameState.WAITING);
						GameManager.deactiveGame();
					}
					else{
						Client.myInfo().setState(clientState.INGAME);
						GameManager.setStates(GameState.WAITING);
					}
					PlayerTable.loseUpdaterStatus();
					
					
					break;
					
				case "active":
					
					//sends the position of the player to the server so it knows
					//where the player is
					Client.myInfo().setState(clientState.INGAME);
					Report.notify("game starting!");
					GameManager.setStates(GameState.MULTIGAME);
					break;
					
				case "gameleft":
					
					Client.myInfo().setState(clientState.NOTINGAME);
					Report.notify("You have left the game");
					GameManager.setStates(GameState.EDITOR);
					GameManager.clearGame();
					break;
					
					
				case "enemyjoined":
					
					int enemyid = Integer.parseInt(server.readLine());
					int x = Integer.parseInt(server.readLine());
					int y = Integer.parseInt(server.readLine());
					PlayerTable.addEnemy(enemyid, x ,y);
					break;
					
				case "enemyupdater":
					
					PlayerTable.makeEnemyUpdater();
					Report.notify("enemy update");
					break;
					
				case "map":
					
					//reads in the map from the server so everyone gets the same one
					int iDimension = Integer.parseInt(server.readLine());
					int jDimension = Integer.parseInt(server.readLine());
					char[][] map = new char[iDimension][jDimension];
					for(int i = 0; i < iDimension; i ++){
						for(int j = 0; j < jDimension; j++){
							map[i][j] = server.readLine().charAt(0);
						}
					}
					Report.notify("recieved the map");
					GameManager.setMap(map);
					break;
					
				case "tostart":
					
					int playerID = Integer.parseInt(server.readLine());
					PlayerTable.sendToStart(playerID);
					break;
					
				case "playerjoined":
					int idd = Integer.parseInt(server.readLine());
					PlayerTable.addPlayer(idd);
					Report.notify("A player has joined " + idd);
					break;
					
				case "playerleft":
					
					PlayerTable.removePlayer(Integer.parseInt(server.readLine()));
					break;
					
				case "lobbyUpdate":
					
					LobbyInfo.updateLobby(server.readLine());
					LobbyInfo.displayLobby();
					break;
					
				case "lose":
					
					System.out.println("you have lost");
					GameManager.setStates(GameState.LOSE);
					Client.myInfo().setState(clientState.NOTINGAME);
					GameManager.clearGame();
					break;
					
				case "win":
					
					System.out.println("you have won");
					GameManager.setStates(GameState.WIN);
					Client.myInfo().setState(clientState.NOTINGAME);
					GameManager.clearGame();
					break;
					
				case "id":
					
					int id = Integer.parseInt(server.readLine());
					Client.myInfo().setID(id);
					Report.notify("my ID is " + Client.myInfo().getID().get());
					break;
					
				case "move":
					
					String direction = server.readLine();
					playerID = Integer.parseInt(server.readLine());
					PlayerTable.setLocation(playerID, direction);
					//Report.notify("Player " + playerID + " has moved to the " + direction);
					break;
					
				case "movenemy":
					
					direction = server.readLine();
					playerID = Integer.parseInt(server.readLine());
					PlayerTable.setLocationEnemy(playerID, direction);
					//Report.notify("Player " + playerID + " has moved to the " + direction);
					break;

				default:
					Report.notify(response);
				}
			} catch (IOException e) {
				Report.error("Trouble communicating with server");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
 
}
