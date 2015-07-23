package peakaboo.datasource.plugin;

import peakaboo.datasource.DataSource;

import java.util.function.Consumer;

import bolt.plugin.BoltPlugin;
import fava.signatures.FnGet;

public abstract class AbstractDSP implements DataSource, BoltPlugin
{

	protected FnGet<Boolean>	fn_isAborted;
	protected Consumer<Integer>	fn_readScanCallback;
	protected Consumer<Integer> fn_getScanCountCallback;
		
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	public void setCallbacks(
			Consumer<Integer> getScanCountCallback, 
			Consumer<Integer> readScanCallback,
			FnGet<Boolean> isAborted
		)
	{
		this.fn_readScanCallback = readScanCallback;
		this.fn_isAborted = isAborted;
		this.fn_getScanCountCallback = getScanCountCallback;
	}
	
	protected boolean isAborted()
	{
		return fn_isAborted.f();
	}
	
	protected void newScansRead(int numRead)
	{
		fn_readScanCallback.accept(numRead);
	}
	
	protected void haveScanCount(int scanCount)
	{
		fn_getScanCountCallback.accept(scanCount);
	}
	
	
	/**
	 * Returns a name for this DataSource Plugin
	 */
	public abstract String getDataFormat();
	
	
	/**
	 * Returns a description for this DataSource Plugin
	 */
	public abstract String getDataFormatDescription();
	
}
