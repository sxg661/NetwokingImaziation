package server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import clientServer.CommandQueue;

public class ClientTable {
	private static int max = 0;
	private static ConcurrentMap<Integer, CommandQueue<Response>> queues =
			new ConcurrentHashMap<Integer, CommandQueue<Response>>();
	private static ConcurrentLinkedQueue<Integer> freeIDs = new ConcurrentLinkedQueue<Integer>();
	
	
	/**
	 * occupies an ID and adds a queue to the table for that client
	 * @return new ID
	 */
	public synchronized static int newClientID(){
		int id = occupyID();
		queues.put(id , new CommandQueue<Response>());
		return id;
	}
	
	/**
	 * this takes an ID without making a queue for it (used for the enemies)
	 * @return
	 */
	public synchronized static int occupyID(){
		int id;
		if(freeIDs.size() > 0){
			id = freeIDs.poll();
		}
		else{
			id = max++;
		}
		return id;
	}
	
	/**
	 * checks to see if there is a client using this ID
	 * @param clientID
	 * @return
	 */
	public synchronized static boolean isValidID(int clientID){
		return queues.containsKey(clientID);
	}
	
	/**
	 * gets the queue for a certain clientID
	 * @param clientId
	 * @return
	 */
	public synchronized static CommandQueue<Response> getQueue(Integer clientId){
		return queues.get(clientId);
	}
	
	/**
	 * frees a client ID and removes their queue
	 * @param clientId
	 */
	public synchronized static void removeClient(Integer clientId){
		queues.remove(clientId);
		freeID(clientId);
	}
	
	/**
	 * frees a client ID, but doesn't attempt to remove the queue (for enemies)
	 * @param clientId
	 */
	public synchronized static void freeID(Integer clientId){
		if(clientId == max - 1){
			max--;
		}
		else{freeIDs.offer(clientId);}
	}
	

	/**
	 * Adds a response to all the client queues in the client table.
	 * @param response
	 */
	public synchronized static  void addToAllQueues(Response response){
		for(int i = 0; i < max; i++){
			if (queues.containsKey(i)){
				getQueue(i).offer(response);
			}
		}
	}
	
	
}
