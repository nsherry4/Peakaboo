package peakaboo.datatypes.tasks;


import java.util.Iterator;
import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.eventful.Eventful;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;

/**
 * 
 * This class provides a Thread of execution for a set of {@link Task}s, as well as an easy way for a UI to
 * visualise that set, and an overall description of the job being done. TaskList can return a value from the
 * {@link #doTasks()} method, or the result can be stored elsewhere by the implementation of
 * {@link #doTasks()} or by the the final {@link Task} in the list. A TaskList can be executed in a blocking
 * or non-blocking manner. TaskList exteneds {@link Eventful}, and so {@link PeakabooSimpleListener}s can be added
 * to listen for changes, and check for completion when executing in a non-blocking manner.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 * @param <T>
 *            The kind of data that the TaskList will return from its {@link #doTasks()} method
 */

public abstract class TaskList<T> extends Eventful implements Iterable<Task>
{

	private List<Task>	tasks;
	private T			result;
	private String		description;
	private Thread		worker;
	private boolean		isAbortRequested	= false;
	private boolean		aborted				= false;
	private boolean		completed			= false;


	/**
	 * Create a new Task List
	 * 
	 * @param description
	 *            the description of this list of {@link Task}s. Useful for displaying the progress in a UI.
	 */
	public TaskList(String description)
	{
		tasks = DataTypeFactory.<Task> list();
		result = null;
		this.description = description;

		worker = new Thread(new Runnable() {

			public void run()
			{
				T result = doTasks();
				setResult(result);
			}
		});
		worker.setName(description);

	}


	/**
	 * Gets the result of running this task list. This will only return a valid result after the TaskList has
	 * finished working.
	 * 
	 * @return the result of the TaskList
	 */
	public synchronized T getResult()
	{
		return result;
	}


	/**
	 * Set the result of this TaskList's {@link #doTasks()} method
	 * 
	 * @param result
	 */
	protected synchronized void setResult(T result)
	{
		this.result = result;
		this.completed = !isAbortRequested;
		updateListeners();
	}


	/**
	 * Check to see if this TaskList is completed
	 * 
	 * @return true if this TaskList is completed, false otherwise
	 */
	public boolean getCompleted()
	{
		return completed;
	}


	/**
	 * Adds a new {@link Task} to this TaskList. Tasks are listed in a UI in the order they are added.
	 * 
	 * @param t
	 *            {@link Task} to add.
	 */
	public synchronized void addTask(Task t)
	{

		t.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				updateListeners();
			}
		});
		tasks.add(t);
	}


	/**
	 * Returns an Iterator over the {@link Task}s in this TaskList
	 */
	public synchronized Iterator<Task> iterator()
	{
		return tasks.iterator();
	}


	/**
	 * Gets the description for this TaskList
	 * 
	 * @return TaskList description
	 */
	public String getDescription()
	{
		return description;
	}


	/**
	 * Starts this TaskList working in a non-blocking manner.
	 */
	public synchronized void startWorking()
	{
		if (worker == null) return;
		worker.start();
	}


	/**
	 * Starts this TaskList working in a blocking manner
	 * 
	 * @return value of {@link #getResult()} after the work has been completed.
	 */
	public synchronized T startWorkingBlocking()
	{
		worker.run();
		return getResult();
	}


	/**
	 * Stop this TaskList while working.
	 */
	public void requestAbortWorking()
	{
		isAbortRequested = true;
		updateListeners();
	}


	/**
	 * Methid is used to mark this TaskList as having been aborted. UIs looking to abort a TaskList should not
	 * call this method, but rather call {@link TaskList#requestAbortWorking()}
	 */
	public void aborted()
	{
		aborted = true;
		updateListeners();
	}


	/**
	 * Checks to see if a UI has requested that this TaskList be aborted
	 * 
	 * @return true if an abort request has been received, false otherwise
	 */
	public boolean isAbortRequested()
	{
		return isAbortRequested;
	}

	
	/**
	 * Checks to see if this TaskList has been aborted
	 * 
	 * @return true if it has been aborted, false otherwise
	 */
	public boolean isAborted()
	{
		return aborted;
	}

	/**
	 * removes all references to tasks, and removes references to all listeners
	 */
	public synchronized void finished()
	{
		for (Task t : tasks)
		{
			t.removeAllListeners();
		}
		tasks.clear();
		removeAllListeners();
		worker = null;
	}
	
	/**
	 * When creating a TaskList, this abstract method will be implemented, and the logic for executing a
	 * number of {@link Task}s will be added.
	 * 
	 * @return the result of executing the {@link Task}s.
	 */
	protected abstract T doTasks();
}
