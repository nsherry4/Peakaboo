package org.peakaboo.ui.swing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import org.peakaboo.framework.stratus.components.ui.colour.NotificationDot;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterfaceTitle;

public class PeakabooTabTitle extends TabbedInterfaceTitle {

	private NotificationDot dot;
	private Color colour;
	
	public PeakabooTabTitle(TabbedInterface<?> owner, int width) {
		super(owner, width);
		this.dot = new NotificationDot();
	}

	public void setColour(Color colour) {
		this.colour = colour;
		this.dot.setColour(colour);
		boolean added = List.of(this.getComponents()).contains(dot);
		if (colour != null && !added) {
			this.add(dot, BorderLayout.WEST);
		} else if (colour == null & added) {
			this.remove(dot);
		}
	}

	public Color getColour() {
		return colour;
	}

	
	
	
}
