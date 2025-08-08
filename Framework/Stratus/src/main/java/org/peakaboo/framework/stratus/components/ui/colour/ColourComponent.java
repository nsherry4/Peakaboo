package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

public class ColourComponent extends JComponent {

	public static final int DEFAULT_SIZE = 28;
	protected int size = DEFAULT_SIZE;
	protected Color colour = Color.BLACK;
	
	public ColourComponent() {
		this(DEFAULT_SIZE);
	}
	
	public ColourComponent(int size) {
		this.size = size;
	}
	
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
