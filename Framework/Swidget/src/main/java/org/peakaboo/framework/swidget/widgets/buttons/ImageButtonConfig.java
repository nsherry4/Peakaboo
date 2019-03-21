package org.peakaboo.framework.swidget.widgets.buttons;

import javax.swing.border.Border;

import org.peakaboo.framework.swidget.icons.IconSize;

public class ImageButtonConfig {
	String imagename = null;
	String text = "";
	String tooltip = null;
	public ImageButtonLayout layout = null;
	boolean bordered = true;
	IconSize size = IconSize.BUTTON;
	Border border = null;
	ImageButtonSize buttonSize = null;
	Runnable onAction = null;
}