package peakaboo.fileio.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commonenvironment.AbstractFile;

import fava.Fn;

import peakaboo.fileio.DataSource;
import scitypes.Spectrum;
import scratch.ScratchList;


public class LiveDataSource implements DataSource
{

	float maxEnergy;
	
	//FileBackedList if it can be created, Another implementation if it cannot
	List<Spectrum> scans;
	
	
	public LiveDataSource()
	{
		scans = ScratchList.<Spectrum>create("Peakaboo Live Dataset");
	}
	
	/////////////////////////////////////////////////
	// Data Source
	/////////////////////////////////////////////////
	
	public int estimateDataSourceSize()
	{
		return scans.size();
	}


	public String getDatasetName()
	{
		return "Live Dataset";
	}


	public int getExpectedScanCount()
	{
		return scans.size();
	}


	public float getMaxEnergy()
	{
		// TODO Auto-generated method stub
		return maxEnergy;
	}


	public Spectrum getScanAtIndex(int index)
	{
		return scans.get(index);
	}


	public int getScanCount()
	{
		return scans.size();
	}


	public List<String> getScanNames()
	{
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < getScanCount(); i++)
		{
			names.add("Scan " + i);
		}
		
		return names;
	}



	
	public void setScan(int index, Spectrum scan)
	{
		if (scans.size() <= index)
		{
			for (int i = scans.size(); i < index; i++)
			{
				scans.add(null);
			}
		}
		scans.add(index, scan);
	}


	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		return false;
	}
	
	
}
