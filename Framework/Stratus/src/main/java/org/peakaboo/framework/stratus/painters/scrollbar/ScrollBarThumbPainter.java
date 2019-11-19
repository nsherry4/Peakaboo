package org.peakaboo.framework.stratus.painters.scrollbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.SimpleThemed;
import org.peakaboo.framework.stratus.theme.Theme;

public class ScrollBarThumbPainter extends SimpleThemed implements Painter<JComponent> {

	private Color c; 
	
	public ScrollBarThumbPainter(Theme theme, ButtonState state) {
		super(theme);
		c = getTheme().getScrollHandle();
		if (state == ButtonState.MOUSEOVER) {
			c = Stratus.darken(c, 0.1f);
		}
		if (state == ButtonState.PRESSED) {
			c = getTheme().getHighlight();
		}
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {

    	float pad = 3;
    	float radius = getTheme().borderRadius()*2;
    	radius = Math.min(radius, Math.min(width, height) - (pad*2));
    	
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	g.setPaint(c);
    	Shape thumb = new RoundRectangle2D.Float(pad, pad, width-(pad*2), height-(pad*2), radius, radius);     
    	g.fill(thumb);
		
	}
	
	
}