package peakaboo.fileio.datasource;

import java.util.List;

import fava.signatures.FnEach;
import fava.signatures.FnGet;

import bolt.plugin.BoltPlugin;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.DSRealDimensions;
import peakaboo.fileio.DSMetadata;

public abstract class AbstractDataSourcePlugin extends BoltPlugin implements DataSource
{

	protected FnGet<Boolean>	isAborted;
	protected FnEach<Integer>	readScanCallback;
	protected FnEach<Integer> 	getScanCountCallback;
	
	@Override
	public void initialize()
	{
		
	}

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
	
	
	
	public abstract boolean singleFile();
	
	public abstract boolean canRead(String filename);
	public abstract boolean canRead(List<String> filenames);
	
	public abstract void read(String filename) throws Exception;
	public abstract void read(List<String> filenames) throws Exception;


	
	
}
