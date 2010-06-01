package peakaboo.datatypes.tasks.executor.implementations;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.executor.TaskExecutor;

/**
 * 
 * The SplittingTaskExecutor is a multi-threaded {@link TaskExecutor} which assigns work to the given
 * {@link Task} based on the current thread number. Each thread will call {@link Task#work(int threadNumber)}
 * only once, and will pass it the current thread number. It is up to the implementation of the Task to
 * process all values within the range defined by {@link SplittingTaskExecutor#getBlockStart(int threadNumber)}
 * and {@link SplittingTaskExecutor#getBlockSize(int threadNumber)} in a suitable manner. This allows
 * solutions to reuse allocated objects in order to improve speed, efficiency and overhead by processing
 * multiple tickets at once.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class SplittingTaskExecutor extends TaskExecutor
{



	private int				threadCount;
	protected int			threadsWorking;
	private int				ticketsPerThread;

	private List<Integer>	ticketBlockStart;
	private List<Integer>	ticketBlockSize;


	public SplittingTaskExecutor(int taskSize, Task t)
	{

		super(taskSize, t);
		init(taskSize, t);
	}


	public SplittingTaskExecutor(int taskSize)
	{

		super(taskSize, null);
		init(taskSize, null);
	}


	private void init(int taskSize, Task t)
	{

		this.task = t;
		threadCount = calcNumThreads();


		ticketBlockStart = DataTypeFactory.<Integer> list(threadCount);
		ticketBlockSize = DataTypeFactory.<Integer> list(threadCount);


		ticketsPerThread = taskSize / threadCount;
		int ticketsAssigned = 0;

		// set the ticket counts
		for (int i = 0; i < threadCount - 1; i++) {

			ticketBlockStart.add(ticketsAssigned);
			ticketBlockSize.add(ticketsPerThread);

			ticketsAssigned += ticketsPerThread;
		}
		ticketBlockStart.add(ticketsAssigned);
		ticketBlockSize.add(taskSize - ticketsAssigned);


	}


	/**
	 * Sets the {@link Task} for this {@link SplittingTaskExecutor}. Setting the Task after creation of the
	 * {@link TaskExecutor} allows the associated {@link Task} to query the {@link SplittingTaskExecutor} for
	 * information about the work block for each thread. This method will return without setting the Task if
	 * the current Task's state is not {@link Task.State#UNSTARTED}
	 * 
	 * @param task
	 *            the {@link Task} to execute.
	 */
	public void setTask(Task task)
	{

		if (super.task != null && super.task.getState() != Task.State.UNSTARTED) return;
		super.task = task;
	}


	/**
	 * Returns the starting index for the block of work to be done by the {@link Task} for this thread.
	 * @param threadNum the thread number.
	 * @return the starting index for the associated block of work
	 */
	public int getBlockStart(int threadNum)
	{
		return ticketBlockStart.get(threadNum);
	}

	/**
	 * Returns the size of the block of work to be done by the {@link Task} for this thread.
	 * @param threadNum the thread number.
	 * @return the size of the associated block of work
	 */
	public int getBlockSize(int threadNum)
	{
		return ticketBlockSize.get(threadNum);

	}


	/**
	 * Executes the {@link Task}, blocking until complete. This method will return without executing the Task if the Task is null.
	 */
	@Override
	public void executeBlocking()
	{
		if (super.task == null) return;
		super.executeTask(threadCount);

	}


	@Override
	protected void workForExecutor()
	{
		int thread;
		synchronized (this) {

			thread = threadsWorking++;

		}
		
		super.task.work(thread);

	}

	
	public int getThreadCount()
	{
		return threadCount;
	}


}
