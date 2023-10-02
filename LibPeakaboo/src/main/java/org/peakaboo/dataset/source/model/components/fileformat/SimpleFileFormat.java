package org.peakaboo.dataset.source.model.components.fileformat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.peakaboo.dataset.source.model.datafile.DataFile;

public class SimpleFileFormat implements FileFormat {

	private List<String> extensions;
	private String name, desc;
	private boolean singleFile;
	
	public SimpleFileFormat(boolean singleFile, String name, String desc, String... extensions) {
		this(singleFile, name, desc, Arrays.asList(extensions));
	}
	
	public SimpleFileFormat(boolean singleFile, String name, String desc, List<String> extensions) {
		this.extensions = new ArrayList<>(extensions);
		this.singleFile = singleFile;
		this.name = name;
		this.desc = desc;
	}
	
	@Override
	public List<String> getFileExtensions() {
		return Collections.unmodifiableList(extensions);
	}

	public FileFormatCompatibility compatibility(DataFile datafile) {
		boolean match = extensions.stream()
					.map(ext -> datafile.getFilename().toLowerCase().endsWith(ext.toLowerCase()))
					.reduce(false, (a, b) -> a || b);
		if (match) { return FileFormatCompatibility.MAYBE_BY_FILENAME; }
		return FileFormatCompatibility.NO;
	}

	@Override
	public FileFormatCompatibility compatibility(List<DataFile> datafile) {
		if (singleFile && datafile.size() > 1) { return FileFormatCompatibility.NO; }
		if (datafile.isEmpty()) { return FileFormatCompatibility.NO; }
		boolean match = datafile.stream().map(f -> this.compatibility(f) != FileFormatCompatibility.NO).reduce(true, (a, b) -> a && b);
		if (match) { return FileFormatCompatibility.MAYBE_BY_FILENAME; }
		return FileFormatCompatibility.NO;
	}

	@Override
	public String getFormatName() {
		return name;
	}

	@Override
	public String getFormatDescription() {
		return desc;
	}

}
