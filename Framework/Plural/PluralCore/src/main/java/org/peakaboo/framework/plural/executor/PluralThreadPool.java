package org.peakaboo.framework.plural.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.peakaboo.framework.plural.Plural;



class PluralThreadPool
{

	private PluralThreadPool() {}
	
	private static ExecutorService executorService = Executors.newCachedThreadPool(new PluralThreadFactory());
	
	
	public static void execute(Runnable r, int numThreads){
		
	
		List<Future<?>> futures = new ArrayList<>();
			
		for (int i = 0; i < numThreads; i++) futures.add(executorService.submit(r));
		
		for (Future<?> f : futures){
			try {
				f.get();
			} catch (InterruptedException e) {
				Plural.logger().log(Level.WARNING, "Wait for Future result interrupted", e);
			} catch (ExecutionException e) {
				Plural.logger().log(Level.WARNING, "Future returned an exception", e);
			}
		}
		
	}
	
	
}
