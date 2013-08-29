package peakaboo.datasource.plugin.plugins;

import java.io.File;
import java.util.List;

import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.plugin.AbstractDSP;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import fava.signatures.FnMap;


public class PlainTextDSP extends AbstractDSP
{

	String	datasetName;
	int 	size = 0;
	int		scanSize = -1;

	List<Spectrum> scans;
	
	
	public PlainTextDSP()
	{
		scans = SpectrumList.create(getDataFormat());
	}
	
	public String datasetName()
	{
		return datasetName;
	}

	public float maxEnergy()
	{
		return 0;
	}

	public Spectrum get(int index)
	{
		return scans.get(index);
	}

	public int scanCount()
	{
		return size;
	}

	public List<String> scanNames()
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
		
	
		//Split the input up by line
		FStringInput lines = FStringInput.lines(new File(filename));
		

		while (lines.hasNext())
		{
			String line = lines.next();
			
			if (line == null || isAborted()) break;
			
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
			
			
			scans.add(scan);
			size++;
			
			newScansRead(1);
			
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
	public String getDataFormat()
	{
		return "Peakaboo Plain Text";
	}
	
	@Override
	public String getDataFormatDescription()
	{
		return "Peakaboo Plain Text format is a simple XRF format comprised of rows of space-separated numbers.";
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
	public boolean hasScanDimensions()
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

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException
	{
		throw new UnsupportedOperationException();
	}


	

}
