package org.peakaboo.framework.autodialog.model.style.editors;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class TextBoxStyle extends SimpleStyle<String>{

	public TextBoxStyle() {
		super("text-value", CoreStyle.TEXT_VALUE);
	}

}
