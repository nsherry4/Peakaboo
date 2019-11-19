package org.peakaboo.framework.stratus.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.painters.SimpleThemed;
import org.peakaboo.framework.stratus.theme.Theme;

public class ProgressBarBackgroundPainter extends SimpleThemed implements Painter<JComponent> {

	protected Color c1, c2;
	private boolean enabled=true;
	private float margin = 2; 
		
	public ProgressBarBackgroundPainter(Theme theme, boolean enabled) {
		super(theme);
		this.enabled = enabled;
		c1 = Stratus.darken(getTheme().getControl(), 0.3f);
		c2 = Stratus.darken(getTheme().getControl(), 0.15f);
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	float pad = margin;
    	float radius = getTheme().borderRadius();
    	
    	//Bevel under button
    	g.setPaint(getTheme().getWidgetBevel());
    	Shape bevel = new RoundRectangle2D.Float(pad, pad+2, width-pad*2, height-pad*2, radius, radius);     
    	g.fill(bevel);
    	
    	//Background Fill
    	g.setPaint(new LinearGradientPaint(0, 0, 0, height, new float[] {0, 0.35f}, new Color[] {c1, c2}));
    	Shape border = new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2, radius, radius);
    	g.fill(border);
    	
    	//Border
    	g.setPaint(getTheme().getWidgetBorder());
    	g.draw(border);
    	
    	
    	

    	
		
	}

	
	
}
