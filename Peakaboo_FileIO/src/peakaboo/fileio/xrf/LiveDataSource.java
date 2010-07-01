package peakaboo.fileio.xrf;

import java.io.IOException;
import java.util.List;

import fava.Fn;

import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.temp.TempFileList;


public class LiveDataSource implements DataSource
{

	float maxEnergy;
	
	List<Spectrum> scans;
	List<Integer> badScans;
	
	
	public LiveDataSource()
	{
		try
		{
			scans = new TempFileList<Spectrum>(0, "Peakaboo Live Dataset", Spectrum.getEncoder(), Spectrum.getDecoder());
		}
		catch (IOException e)
		{
			scans = DataTypeFactory.<Spectrum>list();
		}
		
		badScans = DataTypeFactory.<Integer>list();
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
		List<String> names = DataTypeFactory.<String>list();
		for (int i = 0; i < getScanCount(); i++)
		{
			names.add("Scan " + i);
		}
		
		return names;
	}


	public void markScanAsBad(int index)
	{
		badScans.add(index);
		badScans = Fn.unique(badScans);
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


}
