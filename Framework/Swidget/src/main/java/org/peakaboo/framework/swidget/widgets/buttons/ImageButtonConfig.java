package org.peakaboo.framework.swidget.widgets.buttons;

import javax.swing.border.Border;

import org.peakaboo.framework.swidget.icons.IconSize;

public class ImageButtonConfig {
	
	public static enum BORDER_STYLE {
		ALWAYS, ACTIVE, NEVER;
	}
	
	public String imagename = null;
	public String text = "";
	public String tooltip = null;
	public ImageButtonLayout layout = null;
	public BORDER_STYLE bordered = BORDER_STYLE.ALWAYS;
	public IconSize size = IconSize.BUTTON;
	public Border border = null;
	public ImageButtonSize buttonSize = null;
	public Runnable onAction = null;
}