package org.peakaboo.framework.stratus.painters.checkbutton;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.painters.SimpleThemed;
import org.peakaboo.framework.stratus.theme.Theme;

public class CheckPainter extends SimpleThemed implements Painter<JComponent>{

	private int pad = 0;
	private int voffset = 0;
	private boolean enabled = true;
	
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
		
		if (enabled && Stratus.focusedWindow(object)) {
			//check shadow/bevel
			Graphics2D gshadow = (Graphics2D) g.create();
			gshadow.setPaint(getTheme().getControlText());
			gshadow.translate(0, -1f);
			drawCheck(gshadow, width, height);
			//check
			g.setPaint(getTheme().getHighlightText());
			drawCheck(g, width, height);
		} else if (!enabled) {
			g.setPaint(getTheme().getControlTextDisabled());
			drawCheck(g, width, height);
		} else {
			g.setPaint(getTheme().getControlText());
			drawCheck(g, width, height);
		}
		
		
		

		
	}
	
	private void drawCheck(Graphics2D g, int width, int height) {
		Stroke old = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.drawLine(pad, pad+height/2+voffset, pad+width/3, pad+height+voffset);
		g.drawLine(pad+width/3, pad+height+voffset, pad+width, pad+voffset);
		g.setStroke(old);
	}

}
