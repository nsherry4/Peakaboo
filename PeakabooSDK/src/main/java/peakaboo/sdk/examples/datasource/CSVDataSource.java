package peakaboo.sdk.examples.datasource;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import peakaboo.datasource.model.AbstractDataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import peakaboo.datasource.model.components.scandata.SimpleScanData;


public class CSVDataSource extends AbstractDataSource
{

	SimpleScanData		scanData;
	SimpleFileFormat	fileFormat;
	File				file;
	
	public CSVDataSource()
	{
		super();
		
		fileFormat = new SimpleFileFormat(
				true, 
				"Comma Separated Values", 
				"The Comma Separated Value format is a simple XRF format comprised of rows of comma-separated numbers.", 
				"csv");
	}
		


	@Override
	public void read(File file) throws Exception
	{
		Scanner s = null;
		this.file = file;
		this.scanData = new SimpleScanData(file.getName());
		
		int spectrumSize = -1;
		
		try {
			
			String line;
			String[] numbers;
			float[] spectrum;
			
			//create a scanner to read lines from the given file
			s = new Scanner(file).useDelimiter("\n");
						
			//read each line as a separate scan
			while (s.hasNext()) {
				
				//get an array of numbers
				line = s.next();
				numbers = line.split(",");
				
				//if this is the first spectrum read, use it to determine the size that all spectra should be
				if (spectrumSize == -1) {
					spectrumSize = numbers.length;
				}
				
				//create a new spectrum object, and add all the values from the number[] array to it
				spectrum = new float[spectrumSize];
				for (int i = 0; i < spectrumSize; i++)
				{
					spectrum[i] = Float.parseFloat(numbers[i]);
				}
				
				//add the current spectrum to the list of spectra
				scanData.add(spectrum);
				
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
	public void read(List<File> files) throws Exception
	{
		if (files == null) throw new UnsupportedOperationException();
		if (files.size() == 0) throw new UnsupportedOperationException();
		if (files.size() > 1) throw new UnsupportedOperationException();
		
		read(files.get(0));
	}



	@Override
	public Metadata getMetadata() {
		//Unsupported by this DataSource
		return null;
	}


	@Override
	public DataSize getDataSize() {
		//Unsupported by this DataSource
		return null;
	}


	@Override
	public PhysicalSize getPhysicalSize() {
		//Unsupported by this DataSource
		return null;
	}


	@Override
	public FileFormat getFileFormat() {
		return fileFormat;
	}


	@Override
	public ScanData getScanData() {
		return scanData;
	}


	@Override
	public String pluginVersion() {
		return "1.0";
	}


}
