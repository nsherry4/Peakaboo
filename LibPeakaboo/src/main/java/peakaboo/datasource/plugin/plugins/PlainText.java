package peakaboo.datasource.plugin.plugins;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import cyclops.ISpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Group;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import peakaboo.datasource.model.components.scandata.SimpleScanData;
import peakaboo.datasource.model.components.scandata.loaderqueue.LoaderQueue;
import peakaboo.datasource.plugin.AbstractDataSource;


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

	@Override
	public String pluginUUID() {
		return "15d6d0ba-bf38-46b2-bdf7-baddb8e8a277";
	}
	
	
	
	//==============================================
	// DATASOURCE METHODS
	//==============================================

	@Override
	public void read(List<Path> files) throws Exception
	{
		
		if (files == null) throw new UnsupportedOperationException();
		if (files.size() == 0) throw new UnsupportedOperationException();
		if (files.size() > 1) throw new UnsupportedOperationException();
		
		Path file = files.get(0);
		
		int fileSize = (int) Files.size(file);
		int lineEstimate = -1;
		
		scandata = new SimpleScanData(file.getFileName().toString());
		//LoaderQueue will push compression off onto the queue thread
		LoaderQueue queue = scandata.createLoaderQueue(10);
		
		
		Iterator<String> lines = Files.lines(file).iterator();

		while (lines.hasNext())
		{
			String line = lines.next();
			if (lineEstimate == -1) {
				lineEstimate = fileSize / line.length();
				getInteraction().notifyScanCount(lineEstimate);
			}
			
			if (line == null || getInteraction().checkReadAborted()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			String[] entries = line.trim().split("[, \\t]+");
			Spectrum scan = parseLine(entries);
//			Spectrum scan = new ISpectrum(Arrays.asList(line.trim().split("[, \\t]+")).stream().map(s -> {
//				try { return Float.parseFloat(s); } 
//				catch (Exception e) { return 0f; }
//			}).collect(toList()));
			
			
			if (size > 0 && scan.size() != scanSize) 
			{
				throw new Exception("Spectra sizes are not equal");
			}
			else if (size == 0)
			{
				scanSize = scan.size();
			}
			
			
			//scandata.add(scan);
			queue.submit(scan);
			size++;
			
			getInteraction().notifyScanRead(1);
			
		}
		
		queue.finish();
		

	}

	private Spectrum parseLine(String[] entries) {
		Spectrum scan = new ISpectrum(entries.length);
		for (String entry : entries) {
			try {
				scan.add(Float.parseFloat(entry));
			} catch (Exception e) {
				//some kind of error
				scan.add(0f);
			}
		}
		return scan;
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
	public Optional<Group> getParameters(List<Path> paths) {
		return Optional.empty();
	}


	

}
