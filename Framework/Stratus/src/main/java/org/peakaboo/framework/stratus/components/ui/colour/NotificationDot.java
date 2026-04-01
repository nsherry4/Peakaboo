package org.peakaboo.framework.stratus.components.ui.colour;

import org.peakaboo.framework.stratus.api.Stratus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class NotificationDot extends ColourComponent {

	public NotificationDot() {
		this(10);
	}
	
	public NotificationDot(int size) {
		super();
		this.size = size;
	}
	
	public void setColour(Color colour) {
		this.colour = colour;
		repaint();
	}
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		
		g0 = g0.create();
		Graphics2D g = Stratus.modernGraphicsSettings(g0);

		int w = getWidth();
		int h = getHeight();
		
		int xw = w - size;
		int xh = h - size;
		
		int sx = xw/2;
		int sy = xh/2;
		
		if (this.colour != null) {
			g.setColor(this.colour);
			g.fillArc(sx, sy, size, size, 0, 360);
		}
		
		g.dispose();
		
	}
	
}
