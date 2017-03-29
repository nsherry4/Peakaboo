package peakaboo.datasource.plugin.oldclsexample;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Spectrum;

public class CSV extends AbstractDataSource
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
	public void read(String filename) throws Exception
	{
				this.filename = filename;
				
		try (Scanner s = new Scanner(new File(filename))){
			
			s.useDelimiter("\n");
			
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
	public String datasetName()
	{
		return filename;
	}

	@Override
	public float maxEnergy()
	{
		return 0;
	}

	@Override
	public Spectrum get(int index) throws IndexOutOfBoundsException
	{
		return data.get(index);
	}

	@Override
	public int scanCount()
	{
		return data.size();
	}

	@Override
	public List<String> scanNames()
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
	public DataSourceFileFormat getFileFormat() {
		return new SimpleFileFormat(
				true,
				"CSV (Comma Separated Values)",
				"The Comma Separated Value format is a simple XRF format comprised of rows of comma-separated numbers.",
				Arrays.asList("csv", "tsv"));
	}
	
	
	@Override
	public DataSourceMetadata getMetadata() {
		return null;
	}


	@Override
	public DataSourceDimensions getDimensions() {
		return null;
	}


	
	
	public static void main(String[] args)
	{
		
	}




}
