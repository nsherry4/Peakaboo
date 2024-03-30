package org.peakaboo.framework.plural.executor;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {

	private List<Integer>	ticketBlockStart;
	private List<Integer>	ticketBlockSize;
	
	private int				totalBlocks;
	private int				nextBlock = 0;
	
	public TicketManager(int taskSize, int blockSize) {
	
		this.totalBlocks = (int)Math.ceil(taskSize / (double)blockSize);
		
		
		ticketBlockStart = new ArrayList<>(totalBlocks);
		ticketBlockSize = new ArrayList<>(totalBlocks);

		int ticketsAssigned = 0;

		// set the ticket counts
		for (int i = 0; i < totalBlocks - 1; i++) {
		
			ticketBlockStart.add(ticketsAssigned);
			ticketBlockSize.add(blockSize);

			ticketsAssigned += blockSize;
		}
		ticketBlockStart.add(ticketsAssigned);
		ticketBlockSize.add(taskSize - ticketsAssigned);
		
	}
	
	/**
	 * Returns the starting index for the block of work to be done by the {@link PluralMap} for this thread.
	 * @param blockNum the block number.
	 * @return the starting index for the associated block of work
	 */
	public int getBlockStart(int blockNum)
	{
		return ticketBlockStart.get(blockNum);
	}

	/**
	 * Returns the size of the block of work to be done by the {@link PluralMap} for this thread.
	 * @param blockNum the block number.
	 * @return the size of the associated block of work
	 */
	public int getBlockSize(int blockNum)
	{
		return ticketBlockSize.get(blockNum);

	}
	
	public synchronized int getTicketBlockIndex()
	{
		if (nextBlock >= totalBlocks) return -1;
		return nextBlock++;
	}
	
}
