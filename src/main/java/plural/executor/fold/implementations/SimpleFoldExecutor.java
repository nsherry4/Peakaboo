package plural.executor.fold.implementations;


import java.util.List;
import java.util.function.BiFunction;

import plural.executor.ExecutorSet;
import plural.executor.fold.FoldExecutor;
import plural.executor.map.MapExecutor;

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

public class SimpleFoldExecutor<T1> extends FoldExecutor<T1>
{

	public SimpleFoldExecutor(List<T1> sourceData, BiFunction<T1, T1, T1> t)
	{
		super(sourceData, t);
	}

	public SimpleFoldExecutor(List<T1> sourceData, T1 base, BiFunction<T1, T1, T1> t)
	{
		super(sourceData, base, t);
	}
	
	
	@Override
	public int calcNumThreads()
	{
		return 1;
	}


	@Override
	public T1 executeBlocking()
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
			
			if (i == 0) {
				super.result = super.sourceData.get(i);
			} else {
				super.result = super.fold.apply(super.sourceData.get(i), super.result);
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
