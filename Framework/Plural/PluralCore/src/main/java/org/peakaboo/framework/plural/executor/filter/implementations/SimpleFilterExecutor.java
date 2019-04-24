package org.peakaboo.framework.plural.executor.filter.implementations;


import java.util.List;
import java.util.function.Predicate;

import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.executor.filter.FilterExecutor;
import org.peakaboo.framework.plural.executor.map.MapExecutor;


/**
 * 
 * This {@link MapExecutor} executes a given {@link Task} on a single thread, and assigns work to the given
 * {@link Task} based on a ticket (number) representing one element of the problem set as defined by a
 * supplied problem size.This TaskExecutor does not accept a {@link ExecutorSet}, and will not update the given
 * {@link Task} as to the completion progress.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class SimpleFilterExecutor<T1> extends FilterExecutor<T1>
{

	public SimpleFilterExecutor(List<T1> sourceData, Predicate<T1> t)
	{
		super(sourceData, t);
	}


	@Override
	public int calcNumThreads()
	{
		return 1;
	}


	@Override
	public List<T1> executeBlocking()
	{
		super.advanceState();

		workForExecutor();
		
		if (super.executorSet != null && super.executorSet.isAbortRequested()) {
			super.executorSet.aborted(); 
		}

		super.advanceState();
		
		if (super.executorSet != null && super.executorSet.isAborted()) return null;
		return super.result;
		
	}


	@Override
	protected void workForExecutor()
	{

		int percent = 0, lastpercent = 0, workunits = 0;
		for (int i = 0; i < super.getDataSize(); i++) {
			
			if (super.filter.test(super.sourceData.get(i)))
			{
				result.add(super.sourceData.get(i));
			}
						
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
