package org.peakaboo.framework.plural.executor.filter;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

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

public abstract class FilterExecutor<T1> extends AbstractExecutor<List<T1>>
{

	protected Predicate<T1>		filter;
	protected List<T1>				sourceData;
	
	protected List<LinkedList<T1>>	acceptedLists;
	protected List<T1> 				result;
	


	public FilterExecutor(List<T1> sourceData, Predicate<T1> filter)
	{
		super();
		
		this.sourceData = sourceData;
		this.filter = filter;

		acceptedLists = new ArrayList<LinkedList<T1>>();
		result = new ArrayList<T1>();
		
		super.setWorkUnits(sourceData.size());
		
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
	public void setFilter(Predicate<T1> filter)
	{

		if (this.filter != null && super.getState() != ExecutorState.UNSTARTED) return;
		this.filter = filter;
	}

	

	/**
	 * Executes the MapExecutor, waiting until the processing is complete.
	 */
	public abstract List<T1> executeBlocking();

}
