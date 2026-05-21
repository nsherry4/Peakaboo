package org.peakaboo.framework.stratus.laf.painters;

import org.peakaboo.framework.stratus.api.Stratus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;


public class BorderPainter implements Painter<JComponent> {

	protected Color colour;
	protected float size;
	protected float radius;
	
	public BorderPainter(Color colour, float size, float radius) {
		this.colour = colour;
		this.size = size;
		this.radius = radius;
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		Shape border = new RoundRectangle2D.Float(0, 0, width-1, height-1, radius, radius);
		
		g = Stratus.modernGraphicsSettings(g);
		
		g.setPaint(colour);
		g.setStroke(new BasicStroke(size));
		g.draw(border);
		
	}

}
