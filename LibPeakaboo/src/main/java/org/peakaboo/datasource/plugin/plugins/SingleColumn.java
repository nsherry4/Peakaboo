package org.peakaboo.datasource.plugin.plugins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.datasize.SimpleDataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;
import org.peakaboo.datasource.model.components.scandata.SimpleScanData;
import org.peakaboo.datasource.plugin.AbstractDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.SparsedList;
import org.peakaboo.framework.cyclops.Spectrum;

public class SingleColumn extends AbstractDataSource {

	SimpleScanData scandata;
	SimpleDataSize datasize;
	@Override
	public Optional<Group> getParameters(List<Path> paths) {
		return Optional.empty();
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}

	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.of(datasize);
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
			public FileFormatCompatibility compatibility(List<Path> filenames) {
				try {
					//exactly 1 file
					if (filenames.size() != 1) {
						return FileFormatCompatibility.NO;
					}
					Path filename = filenames.get(0);
					
					//no larger than 1MB
					if (Files.size(filename) > 1048576l) {
						return FileFormatCompatibility.NO;
					}
					
					//remove comments, clean up, etc
					List<String> lines = tidy(Files.readAllLines(filename));
					
					//test if it's valid
					if (test(lines)) {
						return FileFormatCompatibility.MAYBE_BY_CONTENTS;
					} else {
						return FileFormatCompatibility.NO;
					}
					
				} catch (IOException e) {
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
	public void read(List<Path> filenames) throws Exception {
		//exactly 1 file
		if (filenames.size() != 1) {
			throw new IllegalArgumentException("This DataSource expects a single file");
		}
		Path filename = filenames.get(0);
		
		//no larger than 1MB
		if (Files.size(filename) > 1048576l) {
			throw new IllegalArgumentException("File is too large");
		}
		
		scandata = new SimpleScanData(filename.getFileName().toString());
		
		//remove comments, clean up, etc
		List<String> lines = tidy(Files.readAllLines(filename));
		
		List<Float> floats = new SparsedList<>(new ArrayList<>());
		
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			int index = Integer.parseInt(parts[0].trim());
			float value = Float.parseFloat(parts[1].trim());
			floats.set(index, value);
		}
		
		Spectrum s = new ISpectrum(floats);
		scandata.add(s);
		
	}

	@Override
	public String pluginVersion() {
		return "0.1";
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
				.map(String::trim)
				.filter(l -> l.length() > 0)
				.collect(Collectors.toList());
	}
	
	private static boolean test(List<String> lines) {
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length != 2) return false;
			if (!isInteger(parts[0].trim())) return false;
			if (!isFloat(parts[1].trim())) return false;
		}
		return true;
	}

	private static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	
	private static boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
