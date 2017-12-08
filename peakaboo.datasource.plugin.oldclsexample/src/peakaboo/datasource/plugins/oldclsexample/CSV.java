package peakaboo.datasource.plugins.oldclsexample;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import scitypes.Spectrum;

public class CSV extends AbstractDataSource implements ScanData
{

	List<Spectrum> 	data;
	int				spectrumSize;
	File			filename;
	
	public CSV()
	{
		super();
		data = new ArrayList<Spectrum>();
	}
		
	
	@Override
	public void read(File file) throws Exception
	{
		this.filename = file;
				
		try (Scanner s = new Scanner(file)){
			
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
	public void read(List<File> files) throws Exception
	{
		if (files == null) throw new UnsupportedOperationException();
		if (files.size() == 0) throw new UnsupportedOperationException();
		if (files.size() > 1) throw new UnsupportedOperationException();
		
		read(files.get(0));
	}

	
	
	
	
	
	
	
	

	
	@Override
	public String datasetName()
	{
		return filename.getName();
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
	public String scanName(int index) {
		return "Scan #" + (index+1);
	}



	
	

	@Override
	public FileFormat getFileFormat() {
		return new SimpleFileFormat(
				true,
				"CSV (Comma Separated Values)",
				"The Comma Separated Value format is a simple XRF format comprised of rows of comma-separated numbers.",
				Arrays.asList("csv", "tsv"));
	}
	
	
	@Override
	public Metadata getMetadata() {
		return null;
	}


	@Override
	public DataSize getDataSize() {
		return null;
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		return null;
	}

	@Override
	public ScanData getScanData() {
		return this;
	}

	
	public static void main(String[] args)
	{
		
	}








}
