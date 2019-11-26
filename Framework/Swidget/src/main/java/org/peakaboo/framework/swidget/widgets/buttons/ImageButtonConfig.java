package org.peakaboo.framework.swidget.widgets.buttons;

import javax.swing.border.Border;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.buttons.components.SwidgetComponentConfig;

public class ImageButtonConfig extends SwidgetComponentConfig {
	
	public static enum BORDER_STYLE {
		ALWAYS, ACTIVE, NEVER;
	}

	public ImageButtonLayout layout = null;
	public BORDER_STYLE bordered = BORDER_STYLE.ALWAYS;
	public Border border = null;
	public ImageButtonSize buttonSize = null;
	
}