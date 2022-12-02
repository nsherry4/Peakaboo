package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

public class ColourComponent extends JComponent {

	protected int size = 32;
	protected Color colour = Color.BLACK;
	
	public Color getColour() {
		return colour;
	}
	
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

}
