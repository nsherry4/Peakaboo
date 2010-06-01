package peakaboo.datatypes.tasks.executor;


import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.implementations.SimpleTaskExecutor;
import peakaboo.datatypes.tasks.executor.implementations.SplittingTaskExecutor;
import peakaboo.datatypes.tasks.executor.implementations.TicketingUITaskExecutor;

/**
 * 
 * A TaskExecutor defines a manner of executing a {@link Task} with a given number of data
 * points. Subclasses can perform the work in a single-threaded manner such as {@link SimpleTaskExecutor}, or
 * in a multi-threaded manner such as {@link SplittingTaskExecutor}. {@link TicketingUITaskExecutor} accepts a
 * {@link TaskList} and will update the given Task with the progress of the processing -- useful for updating
 * a UI.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class TaskExecutor
{

	protected final int	taskSize;
	protected Task		task;


	/**
	 * The size of the data set that the provided {@link Task} will be operating on. Various implementations
	 * may have differing numbers of calls to {@link Task#work(int)} is called based on what the TaskExecutor
	 * implementation expects a Task to do. {@link SplittingTaskExecutor}, for example, will call once per
	 * thread, whereas {@link TicketingUITaskExecutor} will call once per data point.
	 * 
	 * @return number of required iterations.
	 */
	public int getTaskSize()
	{
		return taskSize;
	}


	public TaskExecutor(int taskSize, Task task)
	{
		this.taskSize = taskSize;
		this.task = task;
	}


	/**
	 * Calculates the number of threads that should be used by this {@link TaskExecutor}
	 * 
	 * @param threadsPerCore
	 *            a multiplier that determines the number of threads per processor to use.
	 * @return the total number of threads which should be used.
	 */
	public int calcNumThreads(double threadsPerCore)
	{
		int threads = (int) Math.round(Runtime.getRuntime().availableProcessors() * threadsPerCore);
		if (threads <= 0) threads = 1;
		return threads;
	}


	/**
	 * Calculates the number of threads that should be used by this {@link TaskExecutor}
	 * 
	 * @return the total number of threads which should be used.
	 */
	public int calcNumThreads()
	{
		return calcNumThreads(1.0);
	}


	/**
	 * Executes the TaskExecutor, waiting until the processing is complete.
	 */
	public abstract void executeBlocking();


	/**
	 * Implementations of TaskExecutor should call this method to begin processing with the desired number of
	 * threads. The appropriate number of threads will be acquired, and each will call into
	 * {@link TaskExecutor#workForExecutor()}
	 * 
	 * @param numThreads
	 */
	protected void executeTask(int numThreads)
	{
/*
		List<ThreadWorker> workers = TaskThreadPool.getWorkers(numThreads);
		
		for (ThreadWorker w : workers) {
			try {
				w.workForExecutor(this);
			} catch (InterruptedException e) {
				TaskThreadPool.returnWorker(w);
				workers.remove(w);
				e.printStackTrace();
			}
		}

		for (ThreadWorker w : workers) {
			try {
				w.finishWorkForExecutor(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		TaskThreadPool.returnWorkers(workers);
*/
		
		Runnable r = new Runnable() {
		
			public void run()
			{
				workForExecutor();
			}
		};
		
		TaskThreadPool.execute(r, numThreads);
		
	}


	/**
	 * This method will be called once by each ThreadWorker after being dispatched from
	 * {@link TaskExecutor#executeTask(int threadCount)}. Work should be assigned to the {@link Task} from
	 * here.
	 */
	protected abstract void workForExecutor();
	
	public static void shutdownThreadPool()
	{
		TaskThreadPool.destroyThreadPool();
	}
}
