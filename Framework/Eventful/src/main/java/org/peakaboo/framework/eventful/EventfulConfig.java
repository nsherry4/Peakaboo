package org.peakaboo.framework.eventful;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EventfulConfig {

	public static Consumer<Runnable> uiThreadRunner = r -> {
		throw new RuntimeException("Eventful UI Hook has not been configured");
	};
	
	private static LinkedBlockingQueue<Runnable> globalDeliveryQueue = new LinkedBlockingQueue<>();
	public static void deliver(Runnable job) {
		globalDeliveryQueue.add(job);
		uiThreadRunner.accept(EventfulConfig::uiThreadDrain);
	}
	
	//RUN THIS ON THE UI THREAD
	private static void uiThreadDrain() {
		while (!globalDeliveryQueue.isEmpty()) {
			Runnable job = globalDeliveryQueue.poll();
			job.run();
		}
	}
	
}
