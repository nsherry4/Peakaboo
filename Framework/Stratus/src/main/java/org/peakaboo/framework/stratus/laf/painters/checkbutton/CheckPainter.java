package org.peakaboo.framework.stratus.laf.painters.checkbutton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class CheckPainter extends SimpleThemed implements Painter<JComponent>{

	private int pad = 0;
	private int voffset = 0;
	protected boolean enabled = true;
	
	public CheckPainter(Theme theme, int pad, int voffset, boolean enabled) {
		super(theme);
		this.pad = pad;
		this.voffset = voffset;
		this.enabled = enabled;
	}
	
	public CheckPainter(Theme theme, int pad, int voffset) {
		super(theme);
		this.pad = pad;
		this.voffset = voffset;
	}

	
	@Override
	public void paint(Graphics2D g, JComponent object, int w, int h) {
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		int height = (h-pad*2);
		int width = (w-pad*2);
		
		g.setPaint(getCheckColour(object));
		drawCheck(g, width, height);
	}
	
	protected Color getCheckColour(JComponent object) {
		if (enabled && Stratus.focusedWindow(object)) {
			return getTheme().getHighlightText();
		} else if (!enabled) {
			return getTheme().getControlTextDisabled();
		} else {
			return getTheme().getControlText();
		}
	}
	
	private void drawCheck(Graphics2D g, int width, int height) {
		Stroke old = g.getStroke();
		g.setStroke(new BasicStroke(2));
		Path2D shape = new Path2D.Float();
		shape.moveTo(pad, pad+height/2+voffset);
		shape.lineTo(pad+width/3, pad+height+voffset);
		shape.lineTo(pad+width, pad+voffset);
		g.draw(shape);
		g.setStroke(old);
	}

}
