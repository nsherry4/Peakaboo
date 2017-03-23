package peakaboo.datasource.internal;

import java.util.ArrayList;
import java.util.List;

import peakaboo.common.Version;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.DataSourceScanData;
import scitypes.Spectrum;


public class LiveDataSource implements DataSourceScanData
{

	float maxEnergy;
	
	//File-backed List, if it could be created. Some other kind if not
	List<Spectrum> scans;
	
	
	public LiveDataSource()
	{
		scans = SpectrumList.create(Version.program_name + " Live Dataset");
	}
	
	/////////////////////////////////////////////////
	// Data Source
	/////////////////////////////////////////////////
	

	public String datasetName()
	{
		return "Live Dataset";
	}


	public int getExpectedScanCount()
	{
		return scans.size();
	}


	public float maxEnergy()
	{
		return maxEnergy;
	}


	public Spectrum get(int index)
	{
		return scans.get(index);
	}


	public int scanCount()
	{
		return scans.size();
	}


	public List<String> scanNames()
	{
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < scanCount(); i++)
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


}
