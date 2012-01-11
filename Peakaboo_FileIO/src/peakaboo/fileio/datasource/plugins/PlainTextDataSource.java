package peakaboo.fileio.datasource.plugins;

import java.io.File;
import java.util.List;

import bolt.plugin.Plugin;

import peakaboo.fileio.datasource.AbstractCachedDataSourcePlugin;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import fava.signatures.FnMap;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


@Plugin
public class PlainTextDataSource extends AbstractCachedDataSourcePlugin
{

	String	datasetName;
	int 	size = 0;
	int		scanSize = -1;

	
	public String getDatasetName()
	{
		return datasetName;
	}

	public float getMaxEnergy()
	{
		return 0;
	}

	public Spectrum loadScanAtIndex(int index)
	{
		return null;
	}

	public int getScanCount()
	{
		return size;
	}

	public List<String> getScanNames()
	{
		return new Range(0, size-1).map(new FnMap<Integer, String>(){

			public String f(Integer element)
			{
				return "Scan #" + (element+1);
			}}).toSink();
	}

	

	
	
	
	
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================


	@Override
	public boolean canRead(String filename)
	{
		return filename.toLowerCase().endsWith(".txt");
	}

	@Override
	public boolean canRead(List<String> filenames)
	{
		if (filenames == null) return false;
		if (filenames.size() == 0) return false;
		if (filenames.size() > 1) return false;
		
		return canRead(filenames.get(0));
	}

	@Override
	public void read(String filename) throws Exception
	{

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
			
			
			if (size > 0 && scan.size() != scanSize) 
			{
				throw new Exception("Spectra sizes are not equal");
			}
			else if (size == 0)
			{
				scanSize = scan.size();
			}
			
			
			cache(size, scan);
			size++;
			
			readScanCallback.f(1);
			
		}
	}

	@Override
	public void read(List<String> filenames) throws Exception
	{
		
		if (filenames == null) throw new UnsupportedOperationException();
		if (filenames.size() == 0) throw new UnsupportedOperationException();
		if (filenames.size() > 1) throw new UnsupportedOperationException();
		
		read(filenames.get(0));
	}

	@Override
	public String getPluginName()
	{
		return "Peakaboo Plain Text";
	}

	@Override
	public String getPluginDescription()
	{
		return "This plugin provides support for the plain text data format used by Peakaboo";
	}


	@Override
	public List<String> getFileExtensions()
	{
		return new FList<String>("txt");
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
