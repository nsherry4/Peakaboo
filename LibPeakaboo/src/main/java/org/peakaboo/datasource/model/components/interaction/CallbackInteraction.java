package org.peakaboo.datasource.model.components.interaction;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CallbackInteraction implements Interaction {

	private Consumer<Integer>	fnOpenedScanCallback;
	private Supplier<Boolean>	fnIsAborted;
	private Consumer<Integer>	fnReadScanCallback;
	private Consumer<Integer>	fnGetScanCountCallback;
	
	//TODO: Peakaboo 6 - replace with IntConsumer, BooleanSupplier, etc
	public CallbackInteraction(
			Consumer<Integer> openedScanCallback,
			Consumer<Integer> getScanCountCallback, 
			Consumer<Integer> readScanCallback,
			Supplier<Boolean> isAborted
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
		return fnIsAborted.get();
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
