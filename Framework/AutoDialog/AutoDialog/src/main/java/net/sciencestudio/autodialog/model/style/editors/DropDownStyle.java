package net.sciencestudio.autodialog.model.style.editors;

import net.sciencestudio.autodialog.model.style.CoreStyle;
import net.sciencestudio.autodialog.model.style.SimpleStyle;

public class DropDownStyle<T> extends SimpleStyle<T>{

	public DropDownStyle() {
		super("drop-down", CoreStyle.LIST);
	}

}
