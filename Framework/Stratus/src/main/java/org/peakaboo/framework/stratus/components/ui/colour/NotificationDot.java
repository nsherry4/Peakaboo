package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class NotificationDot extends ColourComponent {

	public NotificationDot() {
		super();
		this.size = 10;
	}
	
	public void setColour(Color colour) {
		this.colour = colour;
		repaint();
	}
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		
		g0 = g0.create();
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

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
