package customdatasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import peakaboo.datasource.plugin.AbstractDSP;

import bolt.plugin.Plugin;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

@Plugin
public class CSV extends AbstractDSP
{

	List<Spectrum> 	data;
	int				spectrumSize;
	String			filename;
	
	public CSV()
	{
		super();
		data = new ArrayList<Spectrum>();
	}
		
	
	@Override
	public boolean canRead(String filename)
	{
		filename = filename.toLowerCase();
		return filename.endsWith(".csv") || filename.endsWith(".txt");
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
	public List<String> getFileExtensions()
	{
		List<String> extensions = new ArrayList<String>();
		extensions.add("csv");
		extensions.add("txt");
		return extensions;
	}

	@Override
	public boolean hasMetadata()
	{
		//this plugin does not support metadata
		//see the javadoc for the DSMetadata interface for more details
		return false;
	}

	@Override
	public boolean hasRealDimensions()
	{
		//this plugin does not support real dimensional data
		//see the javadoc for the DSRealDimensions interface for more details
		return false;
	}

	@Override
	public void read(String filename) throws Exception
	{
		Scanner s = null;
		this.filename = filename;
				
		try {
			
			//create a scanner to read lines from the given file
			s = new Scanner(new File(filename)).useDelimiter("\n");
			String line;
			String[] numbers; 
			Spectrum spectrum;
			

			
			//keep reading lines until there are no more
			while (s.hasNext())
			{
				
				//get an array of numbers
				line = s.next();
				numbers = line.split(",");
				
				//if this is the first spectrum read, use it to determine the size that all
				//spectra should be
				if (data.size() == 0) spectrumSize = numbers.length;
				
				//create a new spectrum object, and add all the values from the number[] array to it
				spectrum = new Spectrum(spectrumSize);
				for (String number : numbers)
				{
					spectrum.add(Float.parseFloat(number));
				}
				//add the current spectrum to the list of spectra
				data.add(spectrum);
				
			}
			
		}
		catch (Exception e)
		{
			//rethrow the exception after the finally block closes the scanner
			throw e;
		}
		finally
		{
			if (s != null) s.close();
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
		return "Comma Separated Values";
	}
	
	@Override
	public String getDataFormatDescription()
	{
		return "The Comma Separated Value format is a simple XRF format comprised of rows of comma-separated numbers.";
	}

	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////
	// Unsupported Operations -- See JavaDoc
	////////////////////////////////////////////
	
	@Override
	public String getDatasetName()
	{
		return filename;
	}

	@Override
	public float getMaxEnergy()
	{
		return 0;
	}

	@Override
	public Spectrum getScanAtIndex(int index) throws IndexOutOfBoundsException
	{
		return data.get(index);
	}

	@Override
	public int getScanCount()
	{
		return data.size();
	}

	@Override
	public List<String> getScanNames()
	{
		//make up names for the scans, since they have no names or timestamps in this format
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++)
		{
			names.add("Scan #" + i);
		}
		return names;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Coord<Integer> getDataDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int arg0)
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
	public String getEndTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExperimentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacilityName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInstrumentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLaboratoryName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProjectName()
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
	public String getSessionName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStartTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTechniqueName()
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int arg0) throws IndexOutOfBoundsException
	{
		throw new UnsupportedOperationException();
	}


	public static void main(String[] args)
	{
		
	}




}
