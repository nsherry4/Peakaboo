package org.peakaboo.framework.autodialog.model.style.editors;

import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.SimpleStyle;

public class LabelStyle extends SimpleStyle<String> {

	public LabelStyle() {
		super("label-value", CoreStyle.LABEL_VALUE);
	}
}
