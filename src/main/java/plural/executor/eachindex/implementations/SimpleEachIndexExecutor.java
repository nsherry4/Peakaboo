package plural.executor.eachindex.implementations;


import java.util.function.Consumer;

import plural.executor.ExecutorState;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.map.MapExecutor;

/**
 * 
 * The PluralMapExecutor is a multi-threaded {@link MapExecutor} which assigns work to the given
 * {@link PluralMap} based on the current thread number.
 * 
 * @author Nathaniel Sherry, 2009-2010
 * 
 */

public class SimpleEachIndexExecutor extends EachIndexExecutor
{

	protected int			threadCount;
	
	public SimpleEachIndexExecutor(int size, Consumer<Integer> pluralEachIndex)
	{
		super(size, pluralEachIndex);
	}


	/**
	 * Sets the {@link PluralMap} for this {@link SplittingMapExecutor}. Setting the PluralMap after creation of the
	 * {@link MapExecutor} allows the associated {@link PluralMap} to query the {@link SplittingMapExecutor} for
	 * information about the work block for each thread. This method will return without setting the PluralMap if
	 * the current PluralMap's state is not {@link PluralMap.ExecutorState#UNSTARTED}
	 * 
	 * @param map
	 *            the {@link PluralMap} to execute.
	 */
	public void setEachIndex(Consumer<Integer> eachIndex)
	{
		if (super.eachIndex != null && super.getState() != ExecutorState.UNSTARTED) return;
		super.eachIndex = eachIndex;
	}

	


	/**
	 * Executes the {@link Task}, blocking until complete. This method will return without executing the Task if the Task is null.
	 */
	@Override
	public void executeBlocking()
	{
		super.advanceState();

		workForExecutor();
		
		if (super.executorSet != null && super.executorSet.isAbortRequested()) {
			super.executorSet.aborted(); 
		}

		super.advanceState();
	}


	

	@Override
	protected void workForExecutor()
	{

		int percent = 0, lastpercent = 0, workunits = 0;
		for (int i = 0; i < super.getDataSize(); i++) {
			
			super.eachIndex.accept(i);
			
			workunits++;
			if (super.executorSet != null) {
				percent = i * 100 / super.getDataSize();
				
				if (percent != lastpercent){
					super.workUnitCompleted(workunits);
					lastpercent = percent;
					workunits = 0;
				}
				if (super.executorSet.isAbortRequested()) return;
			}
		}
		
	}


}
