package plural.executor;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import plural.Plural;
import plural.executor.map.MapExecutor;


/**
 * 
 * ThreadWorker is designed to allow a simple form of thread pooling. Once the thread has been started, it
 * will receive calls from {@link #workForExecutor(MapExecutor)}, executing each request in order. When a
 * request for a given TaskExecutor is completed, calls to {@link #finishWorkForExecutor(MapExecutor)} will
 * no longer block.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

class ThreadWorker extends Thread
{

	// this queue is used to cause this thread to block until an executor is looking to run on it
	private LinkedBlockingQueue<AbstractExecutor>	workQueue	= new LinkedBlockingQueue<>();

	private Map<AbstractExecutor, Semaphore>			locks	= new HashMap<>();


	/**
	 * Submits a TaskExecutor to this ThreadWorker.
	 * 
	 * @param executor
	 *            the {@link MapExecutor} to work for
	 * @throws InterruptedException
	 */
	public void workForExecutor(AbstractExecutor executor) throws InterruptedException
	{
		// this will be used to block called to finishWorkForExecutor until the work for this TaskExecutor has
		// been completed
		Semaphore s = new Semaphore(1);
		s.acquire();

		// modify the two queues
		synchronized (this) {
			locks.put(executor, s);
			workQueue.put(executor);
		}


	}


	/**
	 * Starts the ThreadWorker, which will wait until there is a request from a {@link MapExecutor} before
	 * performing any work
	 */
	@Override
	public void run()
	{
		while (true) {
			AbstractExecutor t;
			try {
				t = workQueue.take();
				if (t == null) break;
				t.workForExecutor();

				locks.get(t).release();

			} catch (InterruptedException e) {
				Plural.logger().log(Level.WARNING, "Thread worker interrupted", e);
			}

		}
	}


	/**
	 * 
	 * Blocks until this thread has completed work for the given {@link MapExecutor}
	 * 
	 * @param executor
	 *            the {@link MapExecutor} to work for
	 * @throws InterruptedException
	 */
	public void finishWorkForExecutor(AbstractExecutor executor) throws InterruptedException
	{
		// get the semaphore from the lock set
		Semaphore s;
		synchronized (this) {
			s = locks.get(executor);
		}

		if (s == null) return;

		s.acquire();
		synchronized (this) {
			locks.remove(executor);
		}
		s.release();

	}


}
