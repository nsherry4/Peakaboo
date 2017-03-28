package peakaboo.datasource.components.fileformat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleFileFormat implements DataSourceFileFormat {

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
	public boolean canRead(String filename) {
		return extensions.stream()
					.map(ext -> filename.toLowerCase().endsWith(ext.toLowerCase()))
					.reduce(false, (a, b) -> a || b);
	}

	@Override
	public boolean canRead(List<String> filenames) {
		if (singleFile && filenames.size() > 1) { return false; }
		if (filenames.size() == 0) { return false; }
		return filenames.stream().map(this::canRead).reduce(true, (a, b) -> a && b);
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
