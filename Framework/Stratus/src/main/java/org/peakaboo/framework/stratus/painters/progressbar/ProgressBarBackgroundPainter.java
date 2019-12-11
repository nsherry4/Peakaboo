package org.peakaboo.framework.stratus.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;
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
	
	static final int HEIGHT = 6;
	
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
    	float radius = Math.max(HEIGHT/2, getTheme().borderRadius());
    	float startx = (height-HEIGHT)/2;
    	
    	//Background Fill
    	g.setPaint(getTheme().getWidget());
    	Shape border = new RoundRectangle2D.Float(pad, startx, width-pad*2, HEIGHT, radius, radius);
    	g.fill(border);
    	
    	//Border
    	g.setPaint(Stratus.darken(getTheme().getWidgetBorder(), 0.1f));
    	g.draw(border);
    	
    	
    	

    	
		
	}

	
	
}
