package org.peakaboo.framework.stratus.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.painters.SimpleThemed;
import org.peakaboo.framework.stratus.theme.Theme;


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
	private Color cBorder, cFill;
	
	public ProgressBarForegroundPainter(Theme theme, boolean enabled, Mode mode) {
		super(theme);
		this.enabled = enabled;
		this.mode = mode;
		
		cFill = getTheme().getHighlight();
		cBorder = Stratus.darken(getTheme().getHighlight(), 0.3f);
		
	}
	
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
    	
		int pad = 2;
		float radius = Math.max(HEIGHT/2, getTheme().borderRadius());
		float startx = (height-HEIGHT)/2;
		
    	//Background Fill
    	g.setPaint(cFill);
    	Shape border = new RoundRectangle2D.Float(pad, startx, width-pad*2, HEIGHT, radius, radius);
    	g.fill(border);
    	
    	//Border
    	g.setPaint(cBorder);
    	g.draw(border);
    	
	}

}
