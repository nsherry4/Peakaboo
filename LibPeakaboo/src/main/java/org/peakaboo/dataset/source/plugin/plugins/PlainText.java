package org.peakaboo.dataset.source.plugin.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.SimpleFileFormat;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.PipelineScanData;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.model.components.scandata.ScanEntry;
import org.peakaboo.dataset.source.plugin.AbstractDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.SparsedList;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


public class PlainText extends AbstractDataSource
{

	private PipelineScanData scandata;
	private List<Integer> sizes = Collections.synchronizedList( new SparsedList<>(new ArrayList<>()) );
	
	
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
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		List<DataInputAdapter> inputs = ctx.inputs();
		
		if (inputs == null) throw new UnsupportedOperationException();
		if (inputs.size() == 0) throw new UnsupportedOperationException();
		if (inputs.size() > 1) throw new UnsupportedOperationException();
		
		DataInputAdapter file = inputs.get(0);
		scandata = new PipelineScanData(file.getBasename());
		
		// Variables for getting a linecount estimate 
		final int LINE_ESTIMATE_SAMPLE_SIZE = 5;
		var filesize = file.size();
		Spectrum linesizes = new ArraySpectrum(LINE_ESTIMATE_SAMPLE_SIZE);
		int linecountEstimate = -1;
		boolean hasLinecountEstimate = false;


		// CSV parser used to read this file
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
		
		InputStream instream = file.getInputStream();

		int index = 0;
		int readcount = 0;
		int notificationInterval = 50;
		for (String[] row : parser.iterate(instream)) {
			int scanIndex = index++;
			
			// Estimating the number of rows by the length of the first n rows against the
			// length of the whole file
			if (!hasLinecountEstimate && filesize.isPresent()) {
				// Track each linesize
				linesizes.add(String.join(" ", row).length());
				
				// After we have a few data points, calculate an average linesize and do the
				// calculation.
				if (readcount == LINE_ESTIMATE_SAMPLE_SIZE) {
					hasLinecountEstimate = true;
					float averageLinesize = linesizes.sum() / linesizes.size();
					float bytes = filesize.get().floatValue();
					linecountEstimate = (int)Math.round(bytes / averageLinesize);
					getInteraction().notifyScanCount(linecountEstimate);
					notificationInterval = (int) Math.min(Math.max(notificationInterval, linecountEstimate / 100f), 1000);
				}
			}
			

			
			char delim = parser.getDetectedFormat().getDelimiter();
			ScanEntry entry = new PlainTextScanEntry(scanIndex, row, delim, sizes);
			
			//Record the size to check later
			scandata.submit(entry);
			readcount++;
			
			// Every so often we check in with a listener-ish component
			if (readcount == notificationInterval) {
				
				// Progress
				getInteraction().notifyScanRead(readcount);
				readcount = 0;
				
				// Check for abort request
				if (getInteraction().checkReadAborted()) {
					scandata.abort();
					break; 
				}
				
			}
			
		}
		
		parser.stopParsing();
		scandata.finish();
		
		//Check and make sure all the scans are the same size
		int channels = -1;
		for (int size : sizes) {
			if (channels < 0) {
				channels = size;
			}
			if (channels != size) {
				throw new DataSourceReadException("Spectra sizes are not equal");
			}
		}
		

	}


	@Override
	public FileFormat getFileFormat() {
		return new SimpleFileFormat(
				true, 
				"Peakaboo Plain Text", 
				"Comma, tab, or space separated values, one spectra per line", 
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
	public Optional<Group> getParameters(List<DataInputAdapter> paths) {
		return Optional.empty();
	}

	
	
	

}

class PlainTextScanEntry implements ScanEntry {

	private int index;
	private String[] entries;
	private char delimiter;
	private Spectrum spectrum;
	private List<Integer> sizes;
	
	/**
	 * Entry for the pipeline which parses the spectrum in the pipeline thread pool when it gets it from this entry
	 * @param index the index of the scan
	 * @param entries the channels in this scan
	 * @param delimiter the plaintext delimiter character
	 * @param sizes a list of sizes used to track per-entry/row sizes for checking later
	 */
	public PlainTextScanEntry(int index, String[] entries, char delimiter, List<Integer> sizes) {
		this.index = index;
		this.entries = entries;
		this.delimiter = delimiter;
		this.sizes = sizes;
	}

	//This will be called as part of the pipeline in a thread
	@Override
	public Spectrum spectrum() {
		if (spectrum == null) {
			//Parse the string entries into a spectrum
			spectrum = parseLine();
			//Set the size of this spectrum in the list used to track this for the whole dataset
			sizes.set(index, spectrum.size());
		}
		return spectrum;
	}

	@Override
	public int index() {
		return index;
	}
	
	
	private Spectrum parseLine() {
		int length = entries.length;

		// remove null values from length count if the delimiter is a space. Double
		// space characters will cause null values that shouldn't really be there.
		if (delimiter == ' ') {
			for (String entry : entries) {
				if (entry == null) length--;
			}
		}
		
		Spectrum scan = new ArraySpectrum(length);
		for (int i = 0; i < entries.length; i++) {
			String entry = entries[i];
			try {
				
				//null entry means duplicate delimiter (eg "0,,0" or "0  0")
				//we need different behaviour for spaces than for *actual* 
				//delimiters
				if (entry == null) {
					if (delimiter == ' ') {
						continue;
					} else {
						scan.add(0f);
					}
				} else {
					scan.add(Float.parseFloat(entry));
				}
			} catch (Exception e) {
				// TODO Poor form to catch all exceptions, better way?
				scan.add(0f);
			}
		}
		return scan;
	}
	
}
