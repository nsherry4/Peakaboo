package peakaboo.datatypes.tasks.executor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import peakaboo.datatypes.DataTypeFactory;


class TaskThreadPool
{

	private static ExecutorService executorService;
	
	public static void execute(Runnable r, int numThreads){
	
		if (executorService == null) executorService = Executors.newCachedThreadPool(new TaskThreadFactory());
		
		List<Future<?>> futures = DataTypeFactory.<Future<?>>list();
		
		for (int i = 0; i < numThreads; i++) futures.add(executorService.submit(r));
		
		for (Future<?> f : futures){
			try {
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void destroyThreadPool()
	{
		executorService.shutdown();
		executorService = null;
	}
	
}
