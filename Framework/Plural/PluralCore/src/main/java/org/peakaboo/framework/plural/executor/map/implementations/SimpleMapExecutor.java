package org.peakaboo.framework.plural.executor.map.implementations;


import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.plural.executor.ExecutorSet;
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

public class SimpleMapExecutor<T1, T2> extends MapExecutor<T1, T2>
{

	public SimpleMapExecutor(List<T1> sourceData, Function<T1, T2> t)
	{
		super(sourceData, t);
	}

	public SimpleMapExecutor(List<T1> sourceData, List<T2> targetList, Function<T1, T2> t)
	{
		super(sourceData, targetList, t);
	}

	@Override
	public int calcNumThreads()
	{
		return 1;
	}


	@Override
	public List<T2> executeBlocking()
	{
		super.advanceState();

		workForExecutor();
		
		if (super.executorSet != null && super.executorSet.isAbortRequested()) {
			super.executorSet.aborted(); 
		}

		super.advanceState();
		
		if (super.executorSet != null && super.executorSet.isAborted()) return null;
		return super.targetList;
		
	}


	@Override
	protected void workForExecutor()
	{
		int percent = 0, lastpercent = 0, workunits = 0;
		for (int i = 0; i < super.getDataSize(); i++) {
			
			super.targetList.set(  i, super.map.apply(super.sourceData.get(i))  );
			
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
