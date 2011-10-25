package peakaboo.fileio.implementations;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import com.esotericsoftware.kryo.serialize.ArraySerializer;

import peakaboo.common.Version;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.KryoScratchList;

import commonenvironment.AbstractFile;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;

import scitypes.Spectrum;


public class PlainTextDataSource implements DataSource
{

	FnGet<Boolean>						isAborted;
	FnEach<Integer>						readScanCallback;
	
	//File-backed List, if it could be created. Some other kind if not
	List<Spectrum>						scandata;
	

	String								datasetName;
	
	public PlainTextDataSource(
			AbstractFile file, 
			FnEach<Integer> getScanCountCallback, 
			FnEach<Integer> readScanCallback, 
			FnGet<Boolean> isAborted
		) throws Exception
	{
		
		this.readScanCallback = readScanCallback;
		this.isAborted = isAborted;
		

		KryoScratchList<Spectrum> newlist = new KryoScratchList<Spectrum>(Version.program_name, Spectrum.class);
		newlist.register(float[].class, new ArraySerializer(newlist.getKryo()));
		scandata = newlist;
		datasetName = new File(file.getFileName()).getName();
		
	
		//we count the number of linebreaks in the file. This will slow down
		//reading marginally, but not by a lot, since the slowest part is
		//human readable to machine readable conversion.
		FList<String> lines = FStringInput.lines(new InputStreamReader(file.getInputStream(), "UTF-8")).toSink();
		getScanCountCallback.f(lines.size());
		
		
		for (String line : lines) {
			
			if (line == null || isAborted.f()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			Spectrum scan = new Spectrum(new FList<String>(line.trim().split("[, \\t]+")).map(new FnMap<String, Float>(){
				
				public Float f(String s)
				{
					try { return Float.parseFloat(s); } 
					catch (Exception e) { return 0f; }
					
				}}));
			
			
			if (scandata.size() > 0 && scan.size() != scandata.get(0).size()) throw new Exception("Spectra sizes are not equal");
			
			scandata.add(scan);
			
			readScanCallback.f(1);
			
		}
		
		
	}
	
	public int estimateDataSourceSize()
	{
		return 1;
	}

	public String getDatasetName()
	{
		return datasetName;
	}

	public int getExpectedScanCount()
	{
		return 1;
	}

	public float getMaxEnergy()
	{
		return 0;
	}

	public Spectrum getScanAtIndex(int index)
	{
		return scandata.get(index);
	}

	public int getScanCount()
	{
		return scandata.size();
	}

	public List<String> getScanNames()
	{
		return new Range(0, scandata.size()-1).map(new FnMap<Integer, String>(){

			public String f(Integer element)
			{
				return "Scan #" + (element+1);
			}}).toSink();
	}

	
	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		if (files.size() != 1) return false;
		if (! files.get(0).getFileName().toLowerCase().endsWith(".txt")) return false;
		
		return true;
		
	}
	
	

}
