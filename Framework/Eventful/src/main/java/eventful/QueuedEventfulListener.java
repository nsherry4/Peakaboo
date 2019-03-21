package eventful;

/**
 * QueuedEventListener counts the number of event notifications, and delivers that
 * count at user-specified intervals
 * @author Nathaniel Sherry, 2011
 *
 */

public abstract class QueuedEventfulListener implements EventfulListener {

	private  int counter = 0;
	private Thread deliveryThread;
	
	public QueuedEventfulListener(final int msDelay) {
		
		deliveryThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					
					synchronized (deliveryThread) {
					
						
						if (counter > 0) changes(counter);
						counter=0;
						
					}				
					
					try {
						Thread.sleep(msDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
		});
		
		deliveryThread.setDaemon(true);
		deliveryThread.start();
		
	}
	
	@Override
	public void change() {
		
		synchronized (deliveryThread) {
			counter++;
		}
		
	}

	public abstract void changes(int changes);
	
}
