package org.peakaboo.ui.swing.mapping.colours;



/*
 * Definitive Guide to Swing for Java 2, Second Edition By John Zukowski ISBN: 1-893115-78-X Publisher: APress
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import org.peakaboo.framework.swidget.widgets.Spacing;


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


	public void paintIcon(Component c, Graphics g_, int x, int y)
	{
		Graphics2D g = (Graphics2D) g_;
		
		//Renderer uses 3px padding, so we draw inside of that
		int pad = Spacing.small;
		int rw = c.getWidth()-1-pad*2;
		int rh = c.getHeight()-1-pad*2;
		int rx = pad;
		int ry = pad;
		
		g.setColor(color);
		g.fillRect(rx, ry, rw, rh);
		g.setColor(new Color(0x333333));
		g.drawRect(rx, ry, rw, rh);
	}
}
