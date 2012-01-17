package peakaboo.datasource.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.serialize.ArraySerializer;

import commonenvironment.AbstractFile;
import fava.functionable.FList;

import peakaboo.common.Version;
import peakaboo.datasource.DSScanData;
import peakaboo.datasource.KryoScratchList;
import scitypes.Spectrum;


public class LiveDataSource implements DSScanData
{

	float maxEnergy;
	
	//File-backed List, if it could be created. Some other kind if not
	List<Spectrum> scans;
	
	
	public LiveDataSource()
	{
		KryoScratchList<Spectrum> newlist;
		try {
			newlist = new KryoScratchList<Spectrum>(Version.program_name + " Live Dataset", Spectrum.class);
			newlist.register(float[].class, new ArraySerializer(newlist.getKryo()));
			scans = newlist;
		} catch (IOException e) {
			scans = new FList<Spectrum>();
		}

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
