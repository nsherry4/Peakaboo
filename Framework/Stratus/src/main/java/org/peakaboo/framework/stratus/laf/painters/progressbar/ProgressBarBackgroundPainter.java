package org.peakaboo.framework.stratus.laf.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ProgressBarBackgroundPainter extends SimpleThemed implements Painter<JComponent> {

	protected Color c1, c2;
	private boolean enabled=true;
	private float margin = 2; 
	
	static final int HEIGHT = 6;
	
	public ProgressBarBackgroundPainter(Theme theme, boolean enabled) {
		super(theme);
		this.enabled = enabled;
		c1 = StratusColour.darken(getTheme().getControl(), 0.3f);
		c2 = StratusColour.darken(getTheme().getControl(), 0.15f);
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
    	g.fillRoundRect((int)pad, (int)startx, (int)(width-pad*2), (int)HEIGHT, (int)radius, (int)radius);
    	
	}

	
	
}
