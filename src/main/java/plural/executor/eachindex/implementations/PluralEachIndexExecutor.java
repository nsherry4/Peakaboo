package plural.executor.eachindex.implementations;


import java.util.function.Consumer;

import plural.executor.TicketManager;
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

public class PluralEachIndexExecutor extends EachIndexExecutor
{

	protected int				threadCount;
	protected TicketManager	ticketManager;


	public PluralEachIndexExecutor(int size, Consumer<Integer> pluralEachIndex)
	{
		super(size, pluralEachIndex);
		
		threadCount = calcNumThreads();
		
	}
	
	public PluralEachIndexExecutor(int size, Consumer<Integer> pluralEachIndex, int threads)
	{
		super(size, pluralEachIndex);
		threadCount = Math.max(threads, 1);
	}



	
	/**
	 * Returns the desired size of a block of work to be done. Subclasses looking to change the behaviour of this
	 * class can overload this method
	 * @return
	 */
	public int getDesiredBlockSize()
	{
		return Math.max(super.getDataSize() / (threadCount * 100), 1);
	}


	/**
	 * Executes the {@link Task}, blocking until complete. This method will return without executing the Task if the Task is null.
	 */
	@Override
	public void executeBlocking()
	{
		if (super.eachIndex == null) return;
		
		super.advanceState();

		super.execute(threadCount);
		
		if (super.executorSet != null && super.executorSet.isAbortRequested()) {
			super.executorSet.aborted(); 
		}

		super.advanceState();

	}


	

	@Override
	protected void workForExecutor()
	{
		synchronized(this){
			if (ticketManager == null) 	ticketManager = new TicketManager(super.getDataSize(), getDesiredBlockSize());
		}

		while (true) {
			
			int blockNum = ticketManager.getTicketBlockIndex();
			if (blockNum == -1) break;
			
			int start, size, end;
			start = ticketManager.getBlockStart(blockNum);
			size = ticketManager.getBlockSize(blockNum);
			end = start + size;
			
			for (int i = start; i < end; i++) { eachIndex.accept(i); }
			
			if (super.executorSet != null) {

				super.workUnitCompleted(size);
				if (super.executorSet.isAbortRequested()) return;
			}

		}
		
	}


}
