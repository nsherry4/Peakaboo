package org.peakaboo.framework.stratus.laf.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;


public class ProgressBarForegroundPainter extends SimpleThemed implements Painter<JComponent> {

	public enum Mode {
		EMPTY,
		INDETERMINATE,
		FULL
	}

	protected Color c1, c2, c3, c4, c5;
	private boolean enabled=true;
	private Mode mode;
	static final int HEIGHT = 6;
	private Color cFill;
	private float radius;
	
	public ProgressBarForegroundPainter(Theme theme, boolean enabled, Mode mode) {
		super(theme);
		this.enabled = enabled;
		this.mode = mode;
		
		cFill = getTheme().getHighlight();
		radius = Math.min(HEIGHT, getTheme().borderRadius());
	}
	
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
    	
		int pad = 2;
		float startx = (height-HEIGHT)/2f;
			
    	//Background Fill
    	g.setPaint(cFill);
    	Shape border = new RoundRectangle2D.Float(pad, startx, width-pad*2, HEIGHT, radius, radius);
    	g.fill(border);
    	
    	//Border
    	//g.setPaint(cBorder);
    	//g.draw(border);
    	
	}

}
