package org.peakaboo.datasource.model.components.interaction;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CallbackInteraction implements Interaction {

	private Consumer<Integer>	fn_openedScanCallback;
	private Supplier<Boolean>	fn_isAborted;
	private Consumer<Integer>	fn_readScanCallback;
	private Consumer<Integer>	fn_getScanCountCallback;
	

	public CallbackInteraction(
			Consumer<Integer> openedScanCallback,
			Consumer<Integer> getScanCountCallback, 
			Consumer<Integer> readScanCallback,
			Supplier<Boolean> isAborted
		)
	{
		this.fn_openedScanCallback = openedScanCallback;
		this.fn_readScanCallback = readScanCallback;
		this.fn_isAborted = isAborted;
		this.fn_getScanCountCallback = getScanCountCallback;
	}
	
	
	@Override
	public boolean checkReadAborted()
	{
		return fn_isAborted.get();
	}
	
	@Override
	public void notifyScanRead(int numRead)
	{
		fn_readScanCallback.accept(numRead);
	}
	
	@Override
	public void notifyScanCount(int scanCount)
	{
		fn_getScanCountCallback.accept(scanCount);
	}
	
	@Override
	public void notifyScanOpened(int scanCount) {
		fn_openedScanCallback.accept(scanCount);
	}

	
}
