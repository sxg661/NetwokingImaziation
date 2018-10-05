package clientServer;

import java.util.concurrent.LinkedBlockingQueue;



/**
 * A blocking queue to store requests and responses to be sent over the network.
 * @author sophieguile
 * @param <T> The type to be stored in the blocking Queue.
 */
public class CommandQueue<T> {
		private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
		
		/*
		 * Adds an item to the queue
		 */
		
		/**
		 * Adds an item to the queue
		 * @param item
		 */
		public void offer(T item) {
		    queue.offer(item);
		}
		 
		/*
		 * Takes the removes the item at the front of the queue
		 * If there is not item in the queue it will loop until there is one 
		 */
		public T take() {
			while (true) {
				try {
		            return(queue.take());
		        }
		        catch (InterruptedException e) {
		            
		        }
			}
		}
}
