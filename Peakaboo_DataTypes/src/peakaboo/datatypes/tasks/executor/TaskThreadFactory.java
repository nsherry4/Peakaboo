package peakaboo.datatypes.tasks.executor;

import java.util.concurrent.ThreadFactory;


public class TaskThreadFactory implements ThreadFactory
{

	public Thread newThread(Runnable r)
	{

		Thread t = new Thread(r);
		t.setPriority(Thread.MIN_PRIORITY);
		return new Thread(r);

	}

}
