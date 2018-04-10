package peakaboo.datasource.model.components.fileformat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	@Override
	public FileFormatCompatibility compatibility(Path path) {
		boolean match = extensions.stream()
					.map(ext -> path.toString().toLowerCase().endsWith(ext.toLowerCase()))
					.reduce(false, (a, b) -> a || b);
		if (match) { return FileFormatCompatibility.MAYBE_BY_FILENAME; }
		return FileFormatCompatibility.NO;
	}

	@Override
	public FileFormatCompatibility compatibility(List<Path> paths) {
		if (singleFile && paths.size() > 1) { return FileFormatCompatibility.NO; }
		if (paths.size() == 0) { return FileFormatCompatibility.NO; }
		boolean match = paths.stream().map(f -> this.compatibility(f) != FileFormatCompatibility.NO).reduce(true, (a, b) -> a && b);
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
