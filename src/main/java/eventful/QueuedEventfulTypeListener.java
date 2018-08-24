package eventful;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * QueuedEventfulTypeListener stores events for up to a user-specified time interval, and
 * delivers the events in a list.
 * @author Nathaniel Sherry, 2011
 *
 * @param <T>
 */

public abstract class QueuedEventfulTypeListener<T> implements EventfulTypeListener<T>{

	private LinkedBlockingQueue<T> eventQueue;
	private Thread deliveryThread;
	
	public QueuedEventfulTypeListener(final int msDelay) {
		
		eventQueue = new LinkedBlockingQueue<T>();
		
		deliveryThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				
				while (true) {
					
					List<T> messages = new ArrayList<T>();
					
					while (true) {
						//block for at least one message
						try {
							messages.add(eventQueue.take());
							break;
						} catch (InterruptedException e) {
							//something tried to wake us from sleep while we were alread awake, waiting for the message
						}
					}
					//drain the rest
					eventQueue.drainTo(messages);
					
					
					deliverMessages(messages);
					
					
					try {
						Thread.sleep(msDelay);
					} catch (InterruptedException e) {
						//woken to force delivery
					}
					
				}//while true
			}
		});//thread
		
		deliveryThread.setDaemon(true);
		deliveryThread.start();
		
	}
	
	
	@Override
	public void change(T message) {
		
		if (skipQueue(message)) {
			
			List<T> messages = new ArrayList<T>();
			messages.add(message);
			changes(messages);
			
		} else {
			
			eventQueue.offer(message);

			//interrupt the sleep call, since this message should get delivered right away
			//it is possible that we will end up forcing delivery after the message has been
			//delivered, since it may have been delivered between calling offer and now.
			//thats not really that important, though
			if (flushQueueForMessage(message)) deliveryThread.interrupt();
			
		}
		
	}
	
	private void deliverMessages(final List<T> messages)
	{
		EventfulConfig.runThread.accept(new Runnable() {
			public void run()	{
				changes(messages);
			}
		});
		
	}
	
	public abstract void changes(List<T> messages);

	
	/**
	 * This method determines if a message should skip the queue and be delivered immediately.
	 * @param message
	 * @return true to skip the queue, false otherwise
	 */
	public abstract boolean skipQueue(T message);
	
	/**
	 * This method determines if the placing of the given message in the queue should cause the
	 * queue to be delivered prematurely 
	 * @param message
	 * @return true to force the queue to be delivered prematurely, false otherwise
	 */
	public abstract boolean flushQueueForMessage(T message);
	
}