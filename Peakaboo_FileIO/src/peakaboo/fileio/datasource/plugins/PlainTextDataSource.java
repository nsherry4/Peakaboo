package peakaboo.fileio.datasource.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import bolt.plugin.Plugin;

import com.esotericsoftware.kryo.serialize.ArraySerializer;

import peakaboo.common.Version;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.KryoScratchList;
import peakaboo.fileio.datasource.AbstractDataSourcePlugin;

import commonenvironment.AbstractFile;
import commonenvironment.Env;
import commonenvironment.IOOperations;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import fava.signatures.FnEach;
import fava.signatures.FnGet;
import fava.signatures.FnMap;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


@Plugin
public class PlainTextDataSource extends AbstractDataSourcePlugin
{

	//File-backed List, if it could be created. Some other kind if not
	List<Spectrum>						scandata;
	String								datasetName;

	
	public String getDatasetName()
	{
		return datasetName;
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
	
	
	
	
	
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================

	@Override
	public boolean singleFile()
	{
		return true;
	}

	@Override
	public boolean canRead(String filename)
	{
		return filename.toLowerCase().endsWith(".txt");
	}

	@Override
	public boolean canRead(List<String> filenames)
	{
		for (String file : filenames)
		{
			if (!canRead(file)) return false;
		}
		
		return true;
	}

	@Override
	public void read(String filename) throws Exception
	{

		KryoScratchList<Spectrum> newlist = new KryoScratchList<Spectrum>(Version.program_name, Spectrum.class);
		newlist.register(float[].class, new ArraySerializer(newlist.getKryo()));
		scandata = newlist;
		datasetName = new File(filename).getName();
		
	
		//we count the number of linebreaks in the file. This will slow down
		//reading marginally, but not by a lot, since the slowest part is
		//human readable to machine readable conversion.
		FList<String> lines = FStringInput.lines(new File(filename)).toSink();
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

	@Override
	public void read(List<String> filenames)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPluginName()
	{
		return "Peakaboo Plain Text Data Loader";
	}

	@Override
	public String getPluginDescription()
	{
		return "This plugin provides support for the plain text data format used by Peakaboo";
	}

	
	
	
	
	
	
	//==============================================
	// UNSUPPORTED METHODS
	//==============================================
	
	
	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealDimensionsUnit()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Integer> getDataDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRealDimensions()
	{
		return false;
	}

	@Override
	public boolean hasMetadata()
	{
		return false;
	}

	@Override
	public String getCreationTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCreator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProjectName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSessionName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacilityName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLaboratoryName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExperimentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInstrumentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTechniqueName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSampleName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScanName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStartTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEndTime()
	{
		throw new UnsupportedOperationException();
	}

}
