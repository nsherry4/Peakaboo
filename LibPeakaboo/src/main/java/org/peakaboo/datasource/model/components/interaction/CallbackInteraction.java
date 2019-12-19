package org.peakaboo.datasource.model.components.interaction;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

public class CallbackInteraction implements Interaction {

	private IntConsumer	fnOpenedScanCallback;
	private BooleanSupplier	fnIsAborted;
	private IntConsumer	fnReadScanCallback;
	private IntConsumer	fnGetScanCountCallback;
	
	public CallbackInteraction(
			IntConsumer openedScanCallback,
			IntConsumer getScanCountCallback, 
			IntConsumer readScanCallback,
			BooleanSupplier isAborted
		)
	{
		this.fnOpenedScanCallback = openedScanCallback;
		this.fnReadScanCallback = readScanCallback;
		this.fnIsAborted = isAborted;
		this.fnGetScanCountCallback = getScanCountCallback;
	}
	
	
	@Override
	public boolean checkReadAborted()
	{
		return fnIsAborted.getAsBoolean();
	}
	
	@Override
	public void notifyScanRead(int numRead)
	{
		fnReadScanCallback.accept(numRead);
	}
	
	@Override
	public void notifyScanCount(int scanCount)
	{
		fnGetScanCountCallback.accept(scanCount);
	}
	
	@Override
	public void notifyScanOpened(int scanCount) {
		fnOpenedScanCallback.accept(scanCount);
	}

	
}
