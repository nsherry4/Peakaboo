package swidget.widgets;

import javax.swing.JLabel;

import swidget.icons.IconFactory;
import swidget.icons.IconSize;

public class ToolTipHoverBox extends JLabel {

	public ToolTipHoverBox(String tooltip) {
		super(IconFactory.getImageIcon("hint-symbolic", IconSize.BUTTON));
		this.setToolTipText(tooltip);
		
	}
	
}
