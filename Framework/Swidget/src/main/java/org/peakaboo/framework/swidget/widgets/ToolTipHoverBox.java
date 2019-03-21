package org.peakaboo.framework.swidget.widgets;

import javax.swing.JLabel;

import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;

public class ToolTipHoverBox extends JLabel {

	public ToolTipHoverBox(String tooltip) {
		super(IconFactory.getImageIcon("hint-symbolic", IconSize.BUTTON));
		this.setToolTipText(tooltip);
		
	}
	
}
