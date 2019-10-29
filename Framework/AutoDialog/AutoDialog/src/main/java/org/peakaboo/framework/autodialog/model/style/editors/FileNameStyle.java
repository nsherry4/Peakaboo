package org.peakaboo.framework.autodialog.model.style.editors;

import java.util.List;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class FileNameStyle extends SimpleStyle<String>{

	private String title;
	private List<String> exts;
	
	public FileNameStyle() {
		this(null, null);
	}

	public FileNameStyle(String title, List<String> exts) {
		super("file-name", CoreStyle.TEXT_VALUE);
		this.title = title;
		this.exts = exts;
	}
	
	public String getTypeTitle() {
		return title;
	}

	public List<String> getTypeExtensions() {
		return exts;
	}

}
