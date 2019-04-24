package org.peakaboo.framework.stratus.painters.spinner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.Painter;

public class SpinnerArrowPainter implements Painter<JComponent> {

	private Color color;
	private boolean upwards;
	public SpinnerArrowPainter(Color color, boolean upwards) {
		this.color = color;
		this.upwards = upwards;
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		GeneralPath arrow = new GeneralPath();
		
		float modX = width/9f;
		float modY = height/7f;
		
		float midX = width/2f;
		float midY = height/2f;
		float startX = midX - modX;
		float endX = midX + modX;
		float startY = midY - modY;
		float endY = midY + modY;
		
		startX -= 1f;
		midX -= 0.5f;
		
		if (upwards) {
			startY++;
			midY++;
			endY++;
			arrow.moveTo(midX, startY);
			arrow.lineTo(endX, endY);
			arrow.lineTo(startX, endY);
		} else {
			arrow.moveTo(midX, endY);
			arrow.lineTo(endX, startY);
			arrow.lineTo(startX, startY);
		}
		arrow.closePath();
		g.setPaint(color);
		g.fill(arrow);
		
	}
	
}

