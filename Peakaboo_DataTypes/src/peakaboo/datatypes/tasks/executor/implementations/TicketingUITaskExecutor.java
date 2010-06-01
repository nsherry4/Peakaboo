package peakaboo.datatypes.tasks.executor.implementations;


import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.datatypes.tasks.executor.TaskExecutor;

/**
 * 
 * TicketingUITaskExecutor is a multi-threaded {@link TaskExecutor} which assigns work to the given {@link Task}
 * based on a ticket (number) representing one element of the problem set as defined by a supplied problem
 * size. TicketingTaskExecutor accepts a {@link TaskList}, and when one is provided, it will update the given
 * {@link Task} with the number of work units completed, so that a UI can allow users to monitor the progress
 * of this TicketedTaskExecutor.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class TicketingUITaskExecutor extends TaskExecutor
{

	private Task		task;
	private TaskList<?>	tasklist;
	private int			threadCount;

	private int			currentTicket;
	private int			ticketBlockSize;


	/**
	 * Creates a new TicketingTaskExecutor.
	 * @param ticketCount the number of work tickets needed
	 * @param task the {@link Task} to be executed.
	 * @param tasklist the {@link TaskList} to monitor for an abort status
	 */
	public TicketingUITaskExecutor(int ticketCount, Task task, TaskList<?> tasklist)
	{
		super(ticketCount, task);
		this.task = task;
		this.tasklist = tasklist;
		threadCount = super.calcNumThreads();
		currentTicket = 0;
		ticketBlockSize = Math.max(ticketCount / (threadCount * 50), 1);

		task.setWorkUnits(super.taskSize);
	}


	@Override
	public void executeBlocking()
	{

		super.task.advanceState();

		super.executeTask(threadCount);
		
		if (tasklist != null && tasklist.isAbortRequested()) {
			tasklist.aborted(); 
		}

		super.task.advanceState();
	}


	@Override
	protected void workForExecutor()
	{

		while (true) {

			int blockStart, blockEnd;
		
			synchronized (this) {

				if (currentTicket >= super.taskSize) return;

				blockStart = currentTicket;
				currentTicket += ticketBlockSize;
				blockEnd = currentTicket;

				if (blockEnd > super.taskSize) blockEnd = super.taskSize;

			}

			for (int i = blockStart; i < blockEnd; i++) {
				task.work(i);
			}
			
			if (tasklist != null) {
				task.workUnitCompleted(blockEnd - blockStart);
				if (tasklist.isAbortRequested()) return;
			}

		}


	}

}
