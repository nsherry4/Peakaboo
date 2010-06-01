package peakaboo.datatypes.tasks.executor;


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * ThreadWorker is designed to allow a simple form of thread pooling. Once the thread has been started, it
 * will receive calls from {@link #workForExecutor(TaskExecutor)}, executing each request in order. When a
 * request for a given TaskExecutor is completed, calls to {@link #finishWorkForExecutor(TaskExecutor)} will
 * no longer block.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class ThreadWorkerS extends Thread
{

	// this queue is used to cause this thread to block until an executor is looking to run on it
	private LinkedBlockingQueue<TaskExecutor>	workQueue	= new LinkedBlockingQueue<TaskExecutor>();

	private Map<TaskExecutor, Semaphore>		locks		= DataTypeFactory.<TaskExecutor, Semaphore> map();


	/**
	 * Submits a TaskExecutor to this ThreadWorker.
	 * 
	 * @param executor
	 *            the {@link TaskExecutor} to work for
	 * @throws InterruptedException
	 */
	public void workForExecutor(TaskExecutor executor) throws InterruptedException
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
	 * Starts the ThreadWorker, which will wait until there is a request from a {@link TaskExecutor} before
	 * performing any work
	 */
	@Override
	public void run()
	{
		while (true) {
			TaskExecutor t;
			try {
				t = workQueue.take();
				if (t == null) break;
				t.workForExecutor();

				locks.get(t).release();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	/**
	 * 
	 * Blocks until this thread has completed work for the given {@link TaskExecutor}
	 * 
	 * @param executor
	 *            the {@link TaskExecutor} to work for
	 * @throws InterruptedException
	 */
	public void finishWorkForExecutor(TaskExecutor executor) throws InterruptedException
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
