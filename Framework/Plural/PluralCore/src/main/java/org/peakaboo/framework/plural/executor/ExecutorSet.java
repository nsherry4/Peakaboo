package org.peakaboo.framework.plural.executor;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.peakaboo.framework.eventful.Eventful;
import org.peakaboo.framework.eventful.EventfulListener;

/**
 * 
 * This class provides a Thread of execution for a set of {@link Task}s, as well as an easy way for a UI to
 * visualise that set, and an overall description of the job being done. ExecutorSet can return a value from the
 * {@link #execute()} method, or the result can be stored elsewhere by the implementation of
 * {@link #execute()} or by the the final {@link Task} in the list. A TaskList can be executed in a blocking
 * or non-blocking manner. TaskList exteneds {@link Eventful}, and so {@link EventfulSimpleListener}s can be added
 * to listen for changes, and check for completion when executing in a non-blocking manner.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 * @param <T>
 *            The kind of data that the TaskList will return from its {@link #execute()} method
 */

public abstract class ExecutorSet<T> extends Eventful implements Iterable<PluralExecutor>
{

	private List<PluralExecutor>	executors;
	private T				result;
	private boolean			hasResult			= false;
	private String			description;
	private Thread			worker;
	private boolean			isAbortRequested	= false;
	private boolean			aborted				= false;
	private boolean			completed			= false;


	/**
	 * Create a new Task List
	 * 
	 * @param description
	 *            the description of this list of {@link Task}s. Useful for displaying the progress in a UI.
	 */
	public ExecutorSet(String description)
	{
		executors = new ArrayList<>();
		result = null;
		this.description = description;

		worker = new Thread(() -> {
			T workResult = execute();
			setResult(workResult);
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
	 * Returns true if the result has been set, false otherwise
	 */
	public synchronized boolean isResultSet() {
		return hasResult;
	}
	
	/**
	 * Set the result of this TaskList's {@link #execute()} method
	 * 
	 * @param result
	 */
	protected synchronized void setResult(T result)
	{
		this.result = result;
		this.hasResult = true;
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


	public synchronized void addExecutor(PluralExecutor e, String name)
	{
		e.setName(name);
		addExecutor(e);
	}
	
	

	public synchronized void addExecutor(PluralExecutor e)
	{
		e.setExecutorSet(this);
		
		e.addListener(this::updateListeners);
		executors.add(e);
	}
	
	

	/**
	 * Returns an Iterator over the {@link Task}s in this TaskList
	 */
	public synchronized Iterator<PluralExecutor> iterator()
	{
		return executors.iterator();
	}

	public synchronized ArrayList<PluralExecutor> toList() {
		return new ArrayList<>(executors);
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
		/* invoke run, rather than start, since this is a blocking call */
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
	 * call this method, but rather call {@link ExecutorSet#requestAbortWorking()}
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
	public synchronized void discard()
	{
		for (PluralExecutor e : executors)
		{
			e.removeAllListeners();
		}
		executors.clear();
		removeAllListeners();
		worker = null;
	}
	
	/**
	 * When creating a TaskList, this abstract method will be implemented, and the logic for executing a
	 * number of {@link Task}s will be added.
	 * 
	 * @return the result of executing the {@link Task}s.
	 */
	protected abstract T execute();
}
