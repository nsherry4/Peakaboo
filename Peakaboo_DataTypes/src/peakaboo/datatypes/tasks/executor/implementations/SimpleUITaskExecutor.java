package peakaboo.datatypes.tasks.executor.implementations;

import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;


public class SimpleUITaskExecutor extends SimpleTaskExecutor
{

	private TaskList<?> taskList;
	
	public SimpleUITaskExecutor(int taskSize, Task t, TaskList<?> taskList)
	{
		super(taskSize, t);
		this.taskList = taskList;
		
		t.setWorkUnits(super.taskSize);

	}
	
	@Override
	protected void workForExecutor()
	{

		
		for (int i = 0; i < super.taskSize; i++) {
			super.t.work(i);
			task.workUnitCompleted();
			if (taskList.isAbortRequested()) return;
		}
	}
	
	@Override
	public void executeBlocking()
	{

		super.task.advanceState();

		workForExecutor();
		
		if (taskList != null && taskList.isAbortRequested()) {
			taskList.aborted(); 
		}

		super.task.advanceState();
	}

}
