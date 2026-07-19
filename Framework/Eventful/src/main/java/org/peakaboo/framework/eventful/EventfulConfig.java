package org.peakaboo.framework.eventful;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EventfulConfig {

	private EventfulConfig() {}
	
	public static Consumer<Runnable> uiThreadRunner = r -> r.run();
	
	private static LinkedBlockingQueue<Runnable> globalDeliveryQueue = new LinkedBlockingQueue<>();
	public static void deliver(Runnable job) {
		globalDeliveryQueue.add(job);
		uiThreadRunner.accept(EventfulConfig::uiThreadDrain);
	}
	
	//RUN THIS ON THE UI THREAD
	private static void uiThreadDrain() {
		//With a headless uiThreadRunner this drain can run on several threads at once,
		//so another drainer may empty the queue between an isEmpty check and a poll.
		//Poll-and-null-check so the loser of that race exits cleanly instead of NPEing.
		Runnable job;
		while ((job = globalDeliveryQueue.poll()) != null) {
			job.run();
		}
	}
	
}
