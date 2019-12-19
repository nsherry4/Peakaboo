package org.peakaboo.ui.swing.mapping.colours;



/*
 * Definitive Guide to Swing for Java 2, Second Edition By John Zukowski ISBN: 1-893115-78-X Publisher: APress
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;


class ColourRenderer implements Icon
{

	private Color				color;
	private int					width;
	private int					height;
	private static final int	DEFAULT_WIDTH	= 20;
	private static final int	DEFAULT_HEIGHT	= 20;


	public ColourRenderer(Color color)
	{
		this(color, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}


	private ColourRenderer(Color color, int width, int height)
	{
		this.color = color;
		this.width = width;
		this.height = height;
	}

	public int getIconHeight()
	{
		return height;
	}


	public int getIconWidth()
	{
		return width;
	}


	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(color);
		g.fillRect(0, 0, 1000, 1000);
	}
}
