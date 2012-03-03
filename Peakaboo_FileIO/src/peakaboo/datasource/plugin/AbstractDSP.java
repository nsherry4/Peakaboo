package peakaboo.datasource.plugin;

import peakaboo.datasource.DataSource;
import bolt.plugin.BoltPlugin;
import fava.signatures.FnEach;
import fava.signatures.FnGet;

public abstract class AbstractDSP implements DataSource, BoltPlugin
{

	protected FnGet<Boolean>	fn_isAborted;
	protected FnEach<Integer>	fn_readScanCallback;
	protected FnEach<Integer> 	fn_getScanCountCallback;
		
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	public void setCallbacks(
			FnEach<Integer> getScanCountCallback, 
			FnEach<Integer> readScanCallback,
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
	
	protected void readScanCallback(int numRead)
	{
		fn_readScanCallback.f(numRead);
	}
	
	protected void getScanCountCallback(int scanCount)
	{
		fn_getScanCountCallback.f(scanCount);
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
