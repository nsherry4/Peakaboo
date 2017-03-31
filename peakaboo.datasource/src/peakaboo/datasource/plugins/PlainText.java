package peakaboo.datasource.plugins;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.dimensions.Dimensions;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.scandata.ScanData;
import peakaboo.datasource.components.scandata.SimpleScanData;
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
	public void read(String filename) throws Exception
	{

		scandata = new SimpleScanData(new File(filename).getName());
		
	
		//Split the input up by line
		FStringInput lines = FStringInput.lines(new File(filename));
		

		while (lines.hasNext())
		{
			String line = lines.next();
			
			if (line == null || getInteraction().checkReadAborted()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			Spectrum scan = new Spectrum(new FList<String>(line.trim().split("[, \\t]+")).stream().map(s -> {
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
	public void read(List<String> filenames) throws Exception
	{
		
		if (filenames == null) throw new UnsupportedOperationException();
		if (filenames.size() == 0) throw new UnsupportedOperationException();
		if (filenames.size() > 1) throw new UnsupportedOperationException();
		
		read(filenames.get(0));
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
	public Dimensions getDimensions() {
		return null;
	}


	@Override
	public Metadata getMetadata() {
		return null;
	}


	

}
