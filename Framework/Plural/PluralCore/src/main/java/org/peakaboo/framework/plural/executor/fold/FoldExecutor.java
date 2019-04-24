package org.peakaboo.framework.plural.executor.fold;


import java.util.List;
import java.util.function.BiFunction;

import org.peakaboo.framework.plural.executor.AbstractExecutor;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.executor.ExecutorState;
import org.peakaboo.framework.plural.executor.map.MapExecutor;
import org.peakaboo.framework.plural.executor.map.implementations.PluralMapExecutor;
import org.peakaboo.framework.plural.executor.map.implementations.SimpleMapExecutor;

/**
 * 
 * A TaskExecutor defines a manner of executing a {@link Task} with a given number of data
 * points. Subclasses can perform the work in a single-threaded manner such as {@link SimpleMapExecutor}, or
 * in a multi-threaded manner such as {@link PluralMapExecutor}. {@link PluralUIMapExecutor} accepts a
 * {@link ExecutorSet} and will update the given Task with the progress of the processing -- useful for updating
 * a UI.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class FoldExecutor<T1> extends AbstractExecutor<T1>
{

	protected BiFunction<T1, T1, T1>	fold;
	protected List<T1>					sourceData;
	protected T1						result;
	
	
	public FoldExecutor(List<T1> sourceData, BiFunction<T1, T1, T1> fold)
	{
		super();
		
		this.sourceData = sourceData;
		this.fold = fold;
				
		super.setWorkUnits(sourceData.size());
		
	}
	
	
	public FoldExecutor(List<T1> sourceData, T1 base, BiFunction<T1, T1, T1> fold)
	{
		this(sourceData, fold);
		
		this.result = base;
		
	}
	

	@Override
	public int getDataSize()
	{
		return sourceData.size();
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
	public void setFold(BiFunction<T1, T1, T1> fold)
	{

		if (this.fold != null && super.getState() != ExecutorState.UNSTARTED) return;
		this.fold = fold;
	}
	
	
	public void setBase(T1 base)
	{
		if (super.getState() != ExecutorState.UNSTARTED) return;
		this.result = base;
	}
	
	/**
	 * Executes the MapExecutor, waiting until the processing is complete.
	 */
	public abstract T1 executeBlocking();

}
