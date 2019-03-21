package org.peakaboo.framework.autodialog.model.style.editors;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class CheckBoxStyle extends SimpleStyle<Boolean>{

	public CheckBoxStyle() {
		super("checkbox", CoreStyle.BOOLEAN);
	}

}
