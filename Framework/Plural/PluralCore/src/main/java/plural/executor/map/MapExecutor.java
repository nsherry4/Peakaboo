package plural.executor.map;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import plural.executor.AbstractExecutor;
import plural.executor.ExecutorSet;
import plural.executor.ExecutorState;
import plural.executor.map.implementations.PluralMapExecutor;
import plural.executor.map.implementations.SimpleMapExecutor;

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

public abstract class MapExecutor<T1, T2> extends AbstractExecutor<List<T2>>
{

	protected Function<T1, T2>		map;
	protected List<T1>				sourceData;
	protected List<T2>				targetList;
	

	public MapExecutor(List<T1> sourceData, Function<T1, T2> map)
	{
		this(sourceData, null, map);
	}
	
	public MapExecutor(List<T1> sourceData, List<T2> target, Function<T1, T2> map)
	{
		super();
		
		this.sourceData = sourceData;
		this.map = map;
		this.targetList = target;

		//if the target list is not given, create and populate with nulls
		if (targetList == null)
		{
			targetList = new ArrayList<T2>(sourceData.size());
			for (int i = 0; i < sourceData.size(); i++){ targetList.add(null); }
		}
		
		for (int i = targetList.size(); i < sourceData.size(); i++)
		{
			targetList.add(null);
		}
		
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
	public void setMap(Function<T1, T2> map)
	{

		if (this.map != null && super.getState() != ExecutorState.UNSTARTED) return;
		this.map = map;
	}
	

	/**
	 * Executes the MapExecutor, waiting until the processing is complete.
	 */
	public abstract List<T2> executeBlocking();

}
