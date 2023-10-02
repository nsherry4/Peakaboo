package org.peakaboo.framework.stratus.laf.painters.progressbar;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

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
    	g.fillRoundRect((int)pad, (int)startx, (int)(width-pad*2), (int)HEIGHT, (int)radius, (int)radius);
    	
	}

}
