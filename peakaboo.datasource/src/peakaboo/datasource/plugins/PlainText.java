package peakaboo.datasource.plugins;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fava.functionable.FStringInput;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import peakaboo.datasource.components.scandata.SimpleScanData;
import scitypes.ISpectrum;
import scitypes.Spectrum;


public class PlainText extends AbstractDataSource
{

	int 	size = 0;
	int		scanSize = -1;

	private SimpleScanData scandata;
	
	public PlainText()
	{
	}
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================

	@Override
	public void read(File file) throws Exception
	{

		scandata = new SimpleScanData(file.getName());
		
	
		//Split the input up by line
		FStringInput lines = FStringInput.lines(file);
		

		while (lines.hasNext())
		{
			String line = lines.next();
			
			if (line == null || getInteraction().checkReadAborted()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			Spectrum scan = new ISpectrum(Arrays.asList(line.trim().split("[, \\t]+")).stream().map(s -> {
				try { return Float.parseFloat(s); } 
				catch (Exception e) { return 0f; }
			}).collect(toList()));
			
			
			if (size > 0 && scan.size() != scanSize) 
			{
				throw new Exception("Spectra sizes are not equal");
			}
			else if (size == 0)
			{
				scanSize = scan.size();
			}
			
			
			scandata.add(scan);
			size++;
			
			getInteraction().notifyScanRead(1);
			
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
	public FileFormat getFileFormat() {
		return new SimpleFileFormat(
				true, 
				"Peakaboo Plain Text", 
				"Peakaboo Plain Text format is a simple XRF format comprised of rows of space-separated numbers.", 
				Arrays.asList("txt", "dat", "csv", "tsv"));
	}


	@Override
	public ScanData getScanData() {
		return scandata;
	}

	
	
	
	//==============================================
	// UNSUPPORTED FEATURES
	//==============================================
	
	@Override
	public DataSize getDataSize() {
		return null;
	}


	@Override
	public Metadata getMetadata() {
		return null;
	}



	@Override
	public PhysicalSize getPhysicalSize() {
		return null;
	}


	

}
