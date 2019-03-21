package plural.executor.map.implementations;


import java.util.List;
import java.util.function.Function;

import plural.executor.TicketManager;
import plural.executor.map.MapExecutor;

/**
 * 
 * The PluralMapExecutor is a multi-threaded {@link MapExecutor} which assigns work to the given
 * {@link PluralMap} based on the current thread number.
 * 
 * @author Nathaniel Sherry, 2009-2010
 * 
 */

public class PluralMapExecutor<T1, T2> extends MapExecutor<T1, T2>
{

	protected int			threadCount;
	
	protected TicketManager	ticketManager;


	public PluralMapExecutor(List<T1> sourceData, Function<T1, T2> t)
	{
		super(sourceData, t);
		
		init(-1);
	}


	public PluralMapExecutor(List<T1> sourceData, List<T2> targetList, Function<T1, T2> t)
	{
		super(sourceData, targetList, t);
		
		init(-1);
	}

	
	public PluralMapExecutor(List<T1> sourceData, Function<T1, T2> t, int threads)
	{
		super(sourceData, t);
		
		init(threads);
	}


	public PluralMapExecutor(List<T1> sourceData, List<T2> targetList, Function<T1, T2> t, int threads)
	{
		super(sourceData, targetList, t);
		
		init(threads);
	}
	

	private void init(int threads)
	{

		threadCount = threads <= 0 ? calcNumThreads() : threads;

	}




	
	/**
	 * Returns the desired size of a block of work to be done. Subclasses looking to change the behaviour of this
	 * class can overload this method
	 * @return
	 */
	protected int getDesiredBlockSize()
	{
		return Math.max((int)Math.ceil(super.getDataSize() / ((double)threadCount * 100)), 1);
	}


	/**
	 * Executes the {@link Task}, blocking until complete. This method will return without executing the Task if the Task is null.
	 */
	@Override
	public List<T2> executeBlocking()
	{
		if (super.map == null) return null;
		
		super.advanceState();
		
		
		super.execute(threadCount);
		
		
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

		synchronized(this){
			if (ticketManager == null) ticketManager = new TicketManager(super.getDataSize(), getDesiredBlockSize());
		}
		
		while(true){
			
			int blockNum = ticketManager.getTicketBlockIndex();
			if (blockNum == -1) break;
			
			int start, size, end;
			start = ticketManager.getBlockStart(blockNum);
			size = ticketManager.getBlockSize(blockNum);
			end = start + size;

			
			T2 t2;
			for (int i = start; i < end; i++)
			{
				t2 = map.apply(sourceData.get(i));
				targetList.set(i, t2);
			}
			
			if (super.executorSet != null) {
				super.workUnitCompleted(size);
				if (super.executorSet.isAbortRequested()) return;
			}
			
		}
	}


}
