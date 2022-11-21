package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.Painter;

public class ComboBoxArrowPainter implements Painter<JComponent> {

	private Color color;
	public ComboBoxArrowPainter(Color color) {
		this.color = color;
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		GeneralPath arrow = new GeneralPath();
		
		float modX = width/12f;
		float modY = height/5.5f;
		
		float midX = width/2f;
		float midY = height/2f;
		float startX = midX - modX;
		float endX = midX + modX;
		float startY = midY - modY;
		float endY = midY + modY;
		
		arrow.moveTo(startX, midY);
		arrow.lineTo(endX, startY);
		arrow.lineTo(endX, endY);
		arrow.closePath();
		g.setPaint(color);
		g.fill(arrow);
		
	}
	
}

