package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import clientServer.Report;
import game.GameTable;
import gameLogic.Map;
import server.Response.responseType;
import users.UserTable;


public class ServerReceiver extends Thread {
	private MyClientInfo myclient;
	private BufferedReader client;
	private CommandExecuter commandExecuter;
	
	public ServerReceiver(BufferedReader client, MyClientInfo myclient) {
	   this.client = client;
	   this.myclient = myclient;
	   this.commandExecuter = new CommandExecuter(myclient);
	}
	  
	  public void run(){
		  boolean quit = false;
		  
		  while(!quit){
			  try {
					String command = client.readLine();
					Response response;
					switch(command){
					case "login":
						String name = client.readLine();
						String password = client.readLine();
						commandExecuter.login(name, password);
						break;
					case "register":
						String username = client.readLine();
						String pass1 = client.readLine();
						String pass2 = client.readLine();
						commandExecuter.register(username, pass1, pass2);
						break;
					case "new bullet":
						int bulletID = Integer.parseInt(client.readLine());
						int bulletX = Integer.parseInt(client.readLine());
						int bulletY = Integer.parseInt(client.readLine());
						GameTable.addBullet(
								bulletID, bulletX, bulletY, myclient.myGameID(), myclient.myID());
						break;
					case "logout":
						commandExecuter.logout();
						break;
					case "joingame":
						int id = Integer.parseInt(client.readLine());
						commandExecuter.joinGame(id != -1 ? Optional.of(id) : Optional.empty());
						break;
					case "move":
						String direction = client.readLine();
						commandExecuter.move(direction);
						break;
					case "leavegame":
						commandExecuter.leaveGame();
						break;
					case "win":
						commandExecuter.win();
						break;
					case "updateEnemies":
						if(myclient.isInGame()){
							GameTable.updateEnemies(myclient.myGameID());
						}
						break;
					case "wavegame":
						Response resp = new Response("Client " +
								myclient.myID() + " has waved to the game!");
						GameTable.sendToGame(resp, myclient.myGameID());
						Report.notify(resp.toString());
						break;
					case "wave":
						response = new Response("Client " + 
						        (myclient.isLoggedIn() ? ( myclient.myID() + " : " + myclient.myUsername() ) 
						        		: myclient.myID()) + " has waved!");
						Report.notify(response.toString());
						ClientTable.addToAllQueues(response);
						break;
					case "restart":
						if(myclient.isInGame())
							GameTable.sendToStart(myclient.myID(), myclient.myGameID());
						break;
					case "quit":
						response = new Response(responseType.QUIT);
						if(myclient.isLoggedIn())
							commandExecuter.logout();
						Report.notify("Client " + myclient.myID() + " has disconnected");
						ClientTable.getQueue(myclient.myID()).offer(response);
						quit = true;
						break;

					default:
						Report.error("Client sent invalid command");
						break;
					}
					
				} catch (IOException e) 
				{
					Report.error("Couldn't connect with client");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}



			
	  
	  
}
