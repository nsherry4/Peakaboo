package peakaboo.datasource.plugin.plugins;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.sciencestudio.autodialog.model.Group;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import peakaboo.datasource.model.components.scandata.SimpleScanData;
import peakaboo.datasource.plugin.AbstractDataSource;
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
	public String pluginName() {
		return "Plain Text DataSource Plugin";
	}



	@Override
	public String pluginDescription() {
		return "Loads XRF data from plain text files; one scan per line, with human readable numbers separated by a space, comma, or tab.";
	}



	@Override
	public String pluginVersion() {
		return "1.0";
	}

	
	
	
	//==============================================
	// DATASOURCE METHODS
	//==============================================

	@Override
	public void read(Path file) throws Exception
	{

		scandata = new SimpleScanData(file.getFileName().toString());
		
		Iterator<String> lines = Files.lines(file).iterator();

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
	public void read(List<Path> files) throws Exception
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
	public Optional<DataSize> getDataSize() {
		return Optional.empty();
	}


	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}



	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.empty();
	}


	

}
