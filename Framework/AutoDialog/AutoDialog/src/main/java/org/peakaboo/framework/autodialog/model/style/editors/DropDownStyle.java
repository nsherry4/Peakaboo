package org.peakaboo.framework.autodialog.model.style.editors;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class DropDownStyle<T> extends SimpleStyle<T>{

	public DropDownStyle() {
		super("drop-down", CoreStyle.LIST);
	}

}
