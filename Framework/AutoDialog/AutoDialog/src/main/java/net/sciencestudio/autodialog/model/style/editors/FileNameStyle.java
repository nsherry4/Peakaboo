package net.sciencestudio.autodialog.model.style.editors;

import net.sciencestudio.autodialog.model.style.CoreStyle;
import net.sciencestudio.autodialog.model.style.SimpleStyle;

public class FileNameStyle extends SimpleStyle<String>{

	public FileNameStyle() {
		super("file-name", CoreStyle.TEXT_VALUE);
	}

}
