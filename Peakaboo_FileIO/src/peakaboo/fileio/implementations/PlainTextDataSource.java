package peakaboo.fileio.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import peakaboo.fileio.DataSource;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;
import fava.Fn;
import fava.datatypes.Range;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionGet;
import fava.signatures.FunctionMap;

import scitypes.Spectrum;
import scitypes.filebacked.FileBackedList;


public class PlainTextDataSource implements DataSource
{

	FunctionGet<Boolean>						isAborted;
	FunctionEach<Integer>						readScanCallback;
	FileBackedList<Spectrum>					scandata;
	

	String										datasetName;
	
	public PlainTextDataSource(AbstractFile file, FunctionEach<Integer> readScanCallback, FunctionGet<Boolean> isAborted) throws Exception
	{
		
		this.readScanCallback = readScanCallback;
		this.isAborted = isAborted;
		
		scandata = new FileBackedList<Spectrum>("Peakaboo");
		datasetName = IOOperations.getFileTitle(  file.getFileName()  );
		
		InputStreamReader r = new InputStreamReader(file.getInputStream());
		BufferedReader reader = new BufferedReader(r);
		
		
		String line;
		int count = 0;
		while (true)
		{
			
			line = reader.readLine();
			if (line == null || isAborted.f()) break;
			
			//split on all non-digit characters
			Spectrum scan = new Spectrum(Fn.map(line.split("\\D"), new FunctionMap<String, Float>(){

				public Float f(String s)
				{
					return Float.parseFloat(s);
				}}));
			
			
			scandata.add(scan);
			
			readScanCallback.f(count);		
			count++;
			
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
		return Fn.map(new Range(0, scandata.size()-1), new FunctionMap<Integer, String>(){

			public String f(Integer element)
			{
				return "Scan #" + element;
			}});
	}

	
	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		if (files.size() != 1) return false;
		if (! files.get(0).getFileName().toLowerCase().endsWith(".txt")) return false;
		
		return true;
		
	}
	
	
	public static String extension()
	{
		return "txt";
	}

}
