package plural.executor;

import java.util.concurrent.ThreadFactory;


class PluralThreadFactory implements ThreadFactory
{

	public Thread newThread(Runnable r)
	{

		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		return t;

	}

}
