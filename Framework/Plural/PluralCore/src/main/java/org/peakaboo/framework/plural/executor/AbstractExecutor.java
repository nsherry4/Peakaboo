package org.peakaboo.framework.plural.executor;

import org.peakaboo.framework.eventful.EventfulBeacon;
import org.peakaboo.framework.plural.Plural;


public abstract class AbstractExecutor<T> extends EventfulBeacon implements PluralExecutor{

	private String			name;

	private ExecutorState	state;
	private int				workUnits;
	private int				workUnitsCompleted;
	
	protected ExecutorSet<?>	executorSet;
	
	private boolean			stalling = false; 
	
	
	protected AbstractExecutor() {
		state = ExecutorState.UNSTARTED;
		workUnitsCompleted = 0;
		setWorkUnits(-1);
	}
	
	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#getName()
	 */
	@Override
	public String getName() 
	{
		return name;
	}
	
	@Override
	public void setName(String name) 
	{
		this.name = name;
	}

	
	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#setStalling(boolean)
	 */
	@Override
	public void setStalling(boolean stalling)
	{
		this.stalling = stalling;
		
		if (state == ExecutorState.WORKING && stalling) { state = ExecutorState.STALLED; }
		if (state == ExecutorState.STALLED && !stalling) { state = ExecutorState.WORKING; }
		
		updateListeners();
	}
	
	

	
	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#getState()
	 */
	@Override
	public synchronized ExecutorState getState()
	{
		return state;
	}


	@Override
	public ExecutorSet<?> getExecutorSet() {
		return executorSet;
	}

	@Override
	public void setExecutorSet(ExecutorSet<?> executorSet) {
		this.executorSet = executorSet;
	}

	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#advanceState()
	 */
	@Override
	public synchronized void advanceState()
	{

		switch (state) {
			case UNSTARTED:
				state = stalling? ExecutorState.STALLED : ExecutorState.WORKING;
				break;
			case WORKING:
			case STALLED:
				state = ExecutorState.COMPLETED;
				break;
			default:
				break;
		}

		updateListeners();

	}
	


	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#markTaskSkipped()
	 */
	@Override
	public synchronized void markTaskSkipped()
	{
		state = ExecutorState.SKIPPED;
		updateListeners();
	}


	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#workUnitCompleted()
	 */
	@Override
	public synchronized void workUnitCompleted()
	{
		workUnitCompleted(1);
	}


	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#workUnitCompleted(int)
	 */
	@Override
	public synchronized void workUnitCompleted(int unitCount)
	{
		this.workUnitsCompleted += unitCount;
		updateListeners();
	}

	@Override
	public int getWorkUnitsCompleted() {
		return this.workUnitsCompleted;
	}

	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#getProgress()
	 */
	@Override
	public synchronized double getProgress()
	{
		return ((double) workUnitsCompleted) / workUnits;
	}


	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#getWorkUnits()
	 */
	@Override
	public synchronized int getWorkUnits()
	{
		return workUnits;
	}
	
	



	@Override
	public synchronized void setWorkUnits(int units)
	{
		if (units < workUnitsCompleted) return;
		if (units <= 0) return;
		workUnits = units;
	}
	
	
	
	/**
	 * This method will be called once by each ThreadWorker after being dispatched from
	 * {@link MapExecutor#execute(int threadCount)}. Work should be assigned to the {@link Task} from
	 * here.
	 */
	protected abstract void workForExecutor();


	/**
	 * Calculates the number of threads that should be used by this {@link MapExecutor}
	 * 
	 * @param threadsPerCore
	 *            a multiplier that determines the number of threads per processor to use.
	 * @return the total number of threads which should be used.
	 */
	public int calcNumThreads(double threadsPerCore)
	{
		int threads = (int) Math.round(Plural.cores() * threadsPerCore);
		if (threads <= 0) threads = 1;
		return threads;
	}


	/**
	 * Calculates the number of threads that should be used by this {@link MapExecutor}
	 * 
	 * @return the total number of threads which should be used.
	 */
	public int calcNumThreads()
	{
		return calcNumThreads(1.0);
	}
	
		
	
	/* (non-Javadoc)
	 * @see plural.executor.PluralExecutor#getDataSize()
	 */
	@Override
	public abstract int getDataSize();
	
	
	/**
	 * Executes the Executor, waiting until the processing is complete.
	 */
	public abstract T executeBlocking();
	
}
