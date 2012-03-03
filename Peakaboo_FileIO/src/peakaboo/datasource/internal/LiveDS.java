package peakaboo.datasource.internal;

import java.util.ArrayList;
import java.util.List;

import peakaboo.common.Version;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.interfaces.DSScanData;
import scitypes.Spectrum;


public class LiveDS implements DSScanData
{

	float maxEnergy;
	
	//File-backed List, if it could be created. Some other kind if not
	List<Spectrum> scans;
	
	
	public LiveDS()
	{
		scans = SpectrumList.create(Version.program_name + " Live Dataset");
	}
	
	/////////////////////////////////////////////////
	// Data Source
	/////////////////////////////////////////////////
	

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


}
