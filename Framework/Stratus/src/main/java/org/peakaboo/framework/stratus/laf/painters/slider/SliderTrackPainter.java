package org.peakaboo.framework.stratus.laf.painters.slider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class SliderTrackPainter extends SimpleThemed implements Painter<JComponent> {

	boolean enabled=true;
	public SliderTrackPainter(Theme theme, boolean enabled) {
		super(theme);
		this.enabled = enabled;
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		float radius = Math.max(1.5f, getTheme().borderRadius());
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		Shape area = new RoundRectangle2D.Float(0, height/2-1f, width-1, 3, radius, radius);
		
		//Fill
		if (enabled) {
			g.setPaint(new LinearGradientPaint(0, height/2-1.5f, 0, height/2+1.5f, new float[] {0, 1f}, new Color[] {getTheme().getWidgetBorder(), getTheme().getControl()}));
			g.fill(area);
		}
		
    	//Border
    	if (enabled) {
    		g.setPaint(getTheme().getWidgetBorder());
    	} else {
    		g.setPaint(StratusColour.lighten(getTheme().getWidgetBorder(), 0.1f));
    	}
    	g.draw(area);
    	

		
	}

}
