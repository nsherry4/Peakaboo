package org.peakaboo.dataset.source.plugin.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.datasize.SimpleDataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.components.physicalsize.PhysicalSize;
import org.peakaboo.dataset.source.model.components.scandata.PipelineScanData;
import org.peakaboo.dataset.source.model.components.scandata.ScanData;
import org.peakaboo.dataset.source.plugin.AbstractDataSource;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.SparsedList;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class SingleColumn extends AbstractDataSource {

	private static final String splitPattern = "[,\\s]+";
	
	PipelineScanData scandata;

	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> paths) {
		return Optional.empty();
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}

	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.empty();
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
	}

	@Override
	public FileFormat getFileFormat() {
		return new FileFormat() {
			
			@Override
			public String getFormatName() {
				return "Single Scan Columnar Format";
			}
			
			@Override
			public String getFormatDescription() {
				return "Tries to recognize as many formats with a single point laid out in a column as possible";
			}
			
			@Override
			public List<String> getFileExtensions() {
				return Arrays.asList("txt", "dat");
			}
			
			@Override
			public FileFormatCompatibility compatibility(List<DataInputAdapter> files) {
				try {
					//exactly 1 file
					if (files.size() != 1) {
						OneLog.log(Level.FINE, "Expected exactly one file");
						return FileFormatCompatibility.NO;
					}
					DataInputAdapter filename = files.get(0);
					
					//no larger than 1MB
					if (filename.size().orElse(0l) > 1048576l) {
						OneLog.log(Level.FINE, "Expected a file less than 1MB");
						return FileFormatCompatibility.NO;
					}
					
					List<String> lines = tidy(filename.toLines());

					//test if it's valid
					if (test(lines)) {
						return FileFormatCompatibility.MAYBE_BY_CONTENTS;
					} else {
						OneLog.log(Level.FINE, "File did not meet expected structure");
						return FileFormatCompatibility.NO;
					}
					
				} catch (IOException e) {
					OneLog.log(Level.FINE, "An error occurred while reading this file: " + e.getMessage());
					return FileFormatCompatibility.NO;
				}
			}
		};
	}


	@Override
	public ScanData getScanData() {
		return scandata;
	}

	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		List<DataInputAdapter> files = ctx.inputs();
		
		//exactly 1 file
		if (files.size() != 1) {
			throw new IllegalArgumentException("This DataSource expects a single file");
		}
		DataInputAdapter file = files.get(0);
		
		//no larger than 1MB
		if (file.size().orElse(0l) > 1048576l) {
			throw new IllegalArgumentException("File is too large");
		}
		
		scandata = new PipelineScanData(file.getBasename());
		
		//remove comments, clean up, etc
		List<String> lines = tidy(file.toLines());
		List<Float> floats = new SparsedList<>(new ArrayList<>());
		
		
		String[] parts = lines.get(0).split(splitPattern);
		if (parts.length == 2) {
			SortedMap<Float, Float> entries = new TreeMap<>(Float::compare);
			for (String line : lines) {
				parts = line.split(splitPattern);
				float order = toNumber(parts[0]);
				float value = toNumber(parts[1]);
				entries.put(order, value);
			}
			for (Float value : entries.values()) {
				floats.add(value);
			}
		} else if (parts.length == 1) {
			int index = 0;
			for (String line : lines) {
				float value = toNumber(line);
				floats.set(index++, value);
			}
		} else {
			throw new DataSourceReadException("Invalid file format, expected 1 or 2 entries per line, found " + parts.length);
		}
		
		Spectrum s = new ArraySpectrum(floats);
		scandata.submit(0, s);
		
		scandata.finish();
		
	}

	@Override
	public String pluginVersion() {
		return "0.3";
	}

	@Override
	public String pluginUUID() {
		return "00547cec-65e1-4cb6-957c-f689b4911452";
	}
	
	private static List<String> tidy(List<String> contents) {
		return contents.stream()
				.filter(l -> ! l.startsWith("#"))
				.filter(l -> ! l.startsWith("%"))
				.filter(l -> ! l.startsWith("$"))
				.filter(l -> testLine(l))
				.map(String::trim)
				.filter(l -> l.length() > 0)
				.collect(Collectors.toList());
	}
	
	private static boolean test(List<String> lines) {
		if (lines.size() == 0) { return false; }
		String[] parts = lines.get(0).split(splitPattern);
		if (parts.length == 2) {			
			for (String line : lines) {
				parts = line.split(splitPattern);
				if (parts.length != 2) {
					return false;
				}
				if (!isNumeric(parts[0]) || !isNumeric(parts[1])) {
					return false;
				}
			}
		} else if (parts.length == 1) {
			for (String line : lines) {
				if (!isNumeric(line)) return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	/** Tests an individual line to see if it is one or two numbers **/
	private static boolean testLine(String line) {
		String[] parts = line.split(splitPattern);
		if (parts.length < 1 || parts.length > 2) { return false; }
		for (String part : parts) {
			if (!isNumeric(part)) { return false; }
		}
		return true;
	}
	
	private static boolean isNumeric(String str) {
		try {
			toNumber(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	private static float toNumber(String str) {
		return Float.parseFloat(str.trim());
	}
}
