package org.peakaboo.framework.autodialog.model.style.editors;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class FileNameStyle extends SimpleStyle<String>{

	public FileNameStyle() {
		super("file-name", CoreStyle.TEXT_VALUE);
	}

}
