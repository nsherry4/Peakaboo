package org.peakaboo.framework.swidget.dialogues.fileio;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileNameExtensionFilter;

public class SimpleFileExtension {

	List<String> exts;
	String name;
	
	public SimpleFileExtension(String name, String... exts) {
		this(name, Arrays.asList(exts));
	}
	public SimpleFileExtension(String name, List<String> exts) {
		this.name = name;
		this.exts = exts.stream()
				.map(String::toLowerCase)
				.map(ext -> {
					if (ext.startsWith(".")) {
						return ext.substring(1);
					} else {
						return ext;
					}
				})
				.collect(Collectors.toList());
		
		assert(this.exts.size() >= 1);
	}

	public List<String> getExtensions() {
		return exts;
	}

	public String getName() {
		return name;
	}
	
	public boolean match(String filename) {
		filename = filename.toLowerCase();
		for (String ext : exts) {
			if (filename.endsWith("." + ext)) return true;
		}
		return false;
	}
	
	public FileNameExtensionFilter getFilter() {
		return new FileNameExtensionFilter(getName(), getExtensions().toArray(new String[] {}));
	}
	
	
}
