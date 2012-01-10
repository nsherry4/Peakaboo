package peakaboo.fileio.datasource;

import java.util.List;

import fava.signatures.FnEach;
import fava.signatures.FnGet;

import bolt.plugin.BoltPlugin;
import peakaboo.fileio.DataSource;


public abstract class AbstractDataSourcePlugin extends BoltPlugin implements DataSource
{

	protected FnGet<Boolean>	isAborted;
	protected FnEach<Integer>	readScanCallback;
	protected FnEach<Integer> 	getScanCountCallback;
	

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
		this.readScanCallback = readScanCallback;
		this.isAborted = isAborted;
		this.getScanCountCallback = getScanCountCallback;
	}
	
	
	
}
