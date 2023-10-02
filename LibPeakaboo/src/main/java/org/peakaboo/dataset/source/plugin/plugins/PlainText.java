package org.peakaboo.dataset.source.plugin.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.SimpleFileFormat;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.PipelineScanData;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.datafile.DataFile;
import org.peakaboo.dataset.source.plugin.AbstractDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


public class PlainText extends AbstractDataSource
{

	int 	size = 0;
	int		scanSize = -1;

	private PipelineScanData scandata;
	
	
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
		return "1.1";
	}

	@Override
	public String pluginUUID() {
		return "15d6d0ba-bf38-46b2-bdf7-baddb8e8a277";
	}
	
	
	
	//==============================================
	// DATASOURCE METHODS
	//==============================================

	@Override
	public void read(List<DataFile> files) throws DataSourceReadException, IOException, InterruptedException
	{
		
		if (files == null) throw new UnsupportedOperationException();
		if (files.size() == 0) throw new UnsupportedOperationException();
		if (files.size() > 1) throw new UnsupportedOperationException();
		
		DataFile file = files.get(0);
		
		var filesize = file.size();
		int lineEstimate = -1;
		
		scandata = new PipelineScanData(file.getBasename());
		
		
		
		CsvParserSettings settings = new CsvParserSettings();
		//Note: the order of the delimiters here matters (!) because it will 
		//determine the delimiter chosen in the event of a tie. I believe that
		//delimiter detection is based on frequency analysis. In the case where 
		//the *real* delimiter is ", " the comma and space will appear the same
		//number of times. It is important that the comma appear before the 
		//space in this list so that it is chosen in those cases.
		settings.setDelimiterDetectionEnabled(true, ',', '\t', ' ');
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setMaxColumns(65536);
		settings.setMaxCharsPerColumn(24);
		CsvParser parser = new CsvParser(settings);
		
		InputStream instream = file.getInputStream();;		

		int index = 0;
		int readcount = 0;
		for (String[] row : parser.iterate(instream)) {

			if (lineEstimate == -1 && filesize.isPresent()) {
				lineEstimate = ((int)filesize.get().longValue()) / (String.join(" ", row).length());
				getInteraction().notifyScanCount(lineEstimate);
			}
			
			if (getInteraction().checkReadAborted()) { break; }
			
			Spectrum scan = parseLine(row, parser.getDetectedFormat().getDelimiter());
			
			if (size > 0 && scan.size() != scanSize)  {
				throw new DataSourceReadException("Spectra sizes are not equal");
			} else if (size == 0) {
				scanSize = scan.size();
			}
			
			scandata.submit(index++, scan);

			size++;
			readcount++;
			
			if (readcount == 50) {
				getInteraction().notifyScanRead(readcount);
				readcount = 0;
			}
			
		}
		
		
		scandata.finish();
		

	}


	private Spectrum parseLine(String[] entries, char delimiter) {
		int length = entries.length;

		//remove null values from length count if the delimiter is a space
		if (delimiter == ' ') {
			for (String entry : entries) {
				if (entry == null) length--;
			}
		}
		
		Spectrum scan = new ISpectrum(length);
		for (String entry : entries) {
			try {
				
				//null entry means duplicate delimiter (eg "0,,0" or "0  0")
				//we need different behaviour for spaces than for *actual* 
				//delimiters
				if (entry == null) {
					if (delimiter == ' ') {
						continue;
					} else {
						entry = "0";
					}
				}
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
	public Optional<Group> getParameters(List<DataFile> paths) {
		return Optional.empty();
	}

	
	
	

}
