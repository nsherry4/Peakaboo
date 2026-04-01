package org.peakaboo.framework.stratus.laf.painters.scrollbar;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ScrollBarThumbPainter extends SimpleThemed implements Painter<JComponent> {

	private Color c; 
	
	public ScrollBarThumbPainter(Theme theme, ButtonState state) {
		super(theme);
		c = getTheme().getScrollHandle();
		if (state == ButtonState.MOUSEOVER) {
			c = StratusColour.darken(c, 0.1f);
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
		
		g = Stratus.modernGraphicsSettings(g);
    	
    	g.setPaint(c);
    	g.fillRoundRect((int)pad, (int)pad, (int)(width-(pad*2)), (int)(height-(pad*2)), (int)radius, (int)radius);
	}
	
	
}