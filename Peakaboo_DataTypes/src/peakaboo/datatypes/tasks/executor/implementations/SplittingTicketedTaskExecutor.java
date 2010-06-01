package peakaboo.datatypes.tasks.executor.implementations;


import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.TaskExecutor;

/**
 * 
 * SplittingTicketedTaskExecutor is a multi-threaded {@link TaskExecutor} which assigns work to the given
 * {@link Task} based on a ticket (number) representing one element of the problem set as defined by a
 * supplied problem size. SplittingTicketTaskExecutor is derived from {@link SplittingTaskExecutor}.
 * SplittingTicketedTaskExecutor does not accept a {@link TaskList}, and will not update the given
 * {@link Task} with the number of work units completed. This TaskExecutor will usually provide better
 * performance than the {@link TicketingUITaskExecutor} at the expense of not being suited to working with a UI.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class SplittingTicketedTaskExecutor extends SplittingTaskExecutor
{

	public SplittingTicketedTaskExecutor(int taskSize, Task t)
	{
		super(taskSize, t);
	}


	@Override
	public void workForExecutor()
	{
		int thread;
		synchronized (this) {

			thread = super.threadsWorking++;

		}

		int start = super.getBlockStart(thread);
		int size = super.getBlockSize(thread);

		for (int i = start; i < start + size; i++) {
			super.task.work(i);
		}

	}

}
