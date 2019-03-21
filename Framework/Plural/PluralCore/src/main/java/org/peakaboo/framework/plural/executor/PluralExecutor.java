package org.peakaboo.framework.plural.executor;

import org.peakaboo.framework.plural.executor.map.implementations.PluralMapExecutor;

import eventful.IEventful;

public interface PluralExecutor extends IEventful{

	/**
	 * Gets the name of this Executor
	 * 
	 * @return name of this Executor
	 */
	String getName();
	
	/**
	 * Sets the name of this Executor
	 * @param name the new name of this Executor
	 */
	void setName(String name);

	
	void setStalling(boolean stalling);

	/**
	 * Gets the {@link ExecutorState} of this Executor
	 * 
	 * @return the current State of this Executor
	 */
	ExecutorState getState();


	/**
	 * Advances the {@link ExecutorState} of the current Executor by 1
	 */
	void advanceState();

	/**
	 * Mark this Executor as having been {@link ExecutorState#SKIPPED}
	 */
	void markTaskSkipped();

	/**
	 * Indicate that a work unit has been completed.
	 */
	void workUnitCompleted();
	
	/**
	 * Gets the total number of work units completed so far
	 */
	int getWorkUnitsCompleted();

	/**
	 * Indicate that several work units have been completed
	 * 
	 * @param unitCount
	 *            number of work units completed
	 */
	void workUnitCompleted(int unitCount);

	/**
	 * Gets the progress of this Executor
	 * 
	 * @return the current progress
	 */
	double getProgress();

	/**
	 * Gets the number of work units in this Executor. This will only be a valid value once
	 * {@link #setWorkUnits(int)} has been called, either explicitly, or through something like the
	 * {@link PluralMapExecutor}.
	 * 
	 * @return number of work units
	 */
	int getWorkUnits();

	/**
	 * Sets the number of work units in this Executor. This allows for tracking the progress of this Executor
	 * 
	 * @param units
	 *            total number of work units
	 */
	void setWorkUnits(int units);

	/**
	 * The size of the data set that the provided Executor will be operating on.
	 * 
	 * @return number of required iterations.
	 */
	int getDataSize();

	
	ExecutorSet<?> getExecutorSet();

	void setExecutorSet(ExecutorSet<?> executorSet);
	
}