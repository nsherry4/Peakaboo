package peakaboo.datatypes.tasks.executor.implementations;


import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.TaskExecutor;

/**
 * 
 * This {@link TaskExecutor} executes a given {@link Task} on a single thread, and assigns work to the given
 * {@link Task} based on a ticket (number) representing one element of the problem set as defined by a
 * supplied problem size.This TaskExecutor does not accept a {@link TaskList}, and will not update the given
 * {@link Task} as to the completion progress.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class SimpleTaskExecutor extends TaskExecutor
{

	protected Task	t;


	public SimpleTaskExecutor(int taskSize, Task t)
	{
		super(taskSize, t);
		this.t = t;
	}


	@Override
	public int calcNumThreads()
	{
		return 1;
	}


	@Override
	public void executeBlocking()
	{
		workForExecutor();

	}


	@Override
	protected void workForExecutor()
	{
		for (int i = 0; i < super.taskSize; i++) {
			t.work(i);
		}
	}

}
