package peakaboo.datasource.components.fileformat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleFileFormat implements FileFormat {

	private List<String> extensions;
	private String name, desc;
	private boolean singleFile;
	
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

	@Override
	public FileFormatCompatibility compatibility(String filename) {
		boolean match = extensions.stream()
					.map(ext -> filename.toLowerCase().endsWith(ext.toLowerCase()))
					.reduce(false, (a, b) -> a || b);
		if (match) { return FileFormatCompatibility.MAYBE_BY_FILENAME; }
		return FileFormatCompatibility.NO;
	}

	@Override
	public FileFormatCompatibility compatibility(List<String> filenames) {
		if (singleFile && filenames.size() > 1) { return FileFormatCompatibility.NO; }
		if (filenames.size() == 0) { return FileFormatCompatibility.NO; }
		boolean match = filenames.stream().map(f -> this.compatibility(f) != FileFormatCompatibility.NO).reduce(true, (a, b) -> a && b);
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
