package org.peakaboo.framework.stratus.painters.spinner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.theme.Theme;

public class SpinnerArrowPainter implements Painter<JComponent> {

	private Theme theme;
	private boolean upwards;
	private List<ButtonState> states;
	private Color fg, bg;
	
	public SpinnerArrowPainter(Theme theme, boolean upwards, ButtonState... states) {
		this.theme = theme;
		this.upwards = upwards;
		this.states = Arrays.asList(states);
		if (this.states.contains(ButtonState.DISABLED)) {
			fg = theme.getControlTextDisabled();
		} else {
			fg = theme.getControlText();
		}
		bg = null;
		if (this.states.contains(ButtonState.PRESSED)) {
			bg = theme.getHighlight();
			fg = theme.getHighlightText();
		} else if (this.states.contains(ButtonState.MOUSEOVER)) {
			bg = Stratus.moreTransparent(theme.getHighlight(), 0.75f);
		}
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
		
		GeneralPath arrow = new GeneralPath();
		
		float size = Math.min(width, height) / 3f;
		float half = (int)(size/2f);
		float line = size / 4f;
		
		float midX = width/2f;
		float midY = height/2f;
		float startX = midX - half;
		float startY = midY - half;
		float endX = midX + half;
		float endY = midY + half;
				
		arrow.moveTo(startX, midY);
		arrow.lineTo(endX, midY);
		if (upwards) {
			arrow.moveTo(midX, startY);
			arrow.lineTo(midX, endY);
		}

		if (bg != null) {
			Ellipse2D circle = new Ellipse2D.Float(startX-half, startY-half, size+half*2, size+half*2);
			g.setColor(bg);
			g.fill(circle);
		}
		
		g.setPaint(fg);
		g.setStroke(new BasicStroke(line));
		g.draw(arrow);
		
	}
	
}

