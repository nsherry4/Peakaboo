package peakaboo.datatypes.tasks.copy;

import peakaboo.datatypes.eventful.Eventful;
import peakaboo.datatypes.tasks.executor.implementations.TicketingUITaskExecutor;

/**
 * 
 * A Task object encapsulates a unit of work applied to a generic data point. A Task will process one unique
 * numerical work ticket at a time. This allows it to easily act as a multi-threaded for-loop or the inner
 * block of a map() function in a functional programming language. This can then be used to process large data
 * sets in various ways with minimal effort using an implementation of the {@link TicketingUITaskExecutor} or other
 * means.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class Task extends Eventful implements Cloneable
{

	private String	name;

	/**
	 * The various states that a {@link Task} can be in.
	 * 
	 * @author Nathaniel Sherry, 2009
	 * 
	 */
	public enum State
	{
		COMPLETED, WORKING, STALLED, UNSTARTED, SKIPPED
	}

	protected State	state;
	private int		workUnits;
	private int		workUnitsCompleted;


	/**
	 * Creates a new Task
	 * 
	 * @param name
	 *            the name of this Task. If a UI visualises this Task or {@link TaskList}, this will be the
	 *            name assigned to this Task
	 */
	public Task(String name)
	{
		init(name, -1);
	}


	/**
	 * Creates a new Task with no name
	 */
	public Task()
	{
		init("", -1);
	}



	private void init(String name, int size)
	{
		this.name = name;
		state = State.UNSTARTED;
		workUnitsCompleted = 0;
		setWorkUnits(size);
	}


	/**
	 * Gets the name of this Task
	 * 
	 * @return name of this Task
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * Gets the {@link State} of this Task
	 * 
	 * @return the current State of this Task
	 */
	public synchronized State getState()
	{
		return state;
	}


	/**
	 * Advances the {@link State} of the current Task by 1
	 */
	public synchronized void advanceState()
	{

		switch (state) {
			case UNSTARTED:
				state = State.WORKING;
				break;
			case WORKING:
				state = State.COMPLETED;
				break;
			default:
				break;
		}

		updateListeners();

	}


	/**
	 * Mark this Task as having been {@link State#SKIPPED}
	 */
	public synchronized void markTaskSkipped()
	{
		state = State.SKIPPED;
		updateListeners();
	}


	/**
	 * Indicate that a work unit has been completed.
	 */
	public synchronized void workUnitCompleted()
	{
		workUnitCompleted(1);
	}


	/**
	 * Indicate that several work units have been completed
	 * 
	 * @param unitCount
	 *            number of work units completed
	 */
	public synchronized void workUnitCompleted(int unitCount)
	{
		this.workUnitsCompleted += unitCount;
		updateListeners();
	}


	/**
	 * Gets the progress of this Task
	 * 
	 * @return the current progress
	 */
	public synchronized double getProgress()
	{
		return ((double) workUnitsCompleted) / workUnits;
	}


	/**
	 * Gets the number of work units in this task. This will only be a valid value once
	 * {@link #setWorkUnits(int)} has been called, either explicitly, or through something like the
	 * {@link TicketingUITaskExecutor}.
	 * 
	 * @return number of work units
	 */
	public synchronized int getWorkUnits()
	{
		return workUnits;
	}


	/**
	 * Sets the number of work units in this task. This allows for tracking the progress of this Task
	 * 
	 * @param units
	 *            total number of work units
	 */
	public synchronized void setWorkUnits(int units)
	{
		if (state == State.WORKING || state == State.STALLED) return;
		if (units < workUnitsCompleted) return;
		if (units < 0) return;
		workUnits = units;
	}


	/**
	 * When creating a Task, this abstract method will be implemented, and the logic for working on a single
	 * item with a given ordinal will be added.
	 * 
	 * @param item
	 *            the item to work on
	 * @param ordinal
	 *            the index of this item from a list provided to something such as a {@link TicketingUITaskExecutor}.
	 * @return true if the work should continue, false if the work should be cancelled.
	 */
	public abstract boolean work(int ordinal);

	
}
