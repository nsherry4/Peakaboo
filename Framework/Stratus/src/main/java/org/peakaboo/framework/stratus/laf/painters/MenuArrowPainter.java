package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;

public class MenuArrowPainter implements Painter<JComponent> {

	private Color color;
	public MenuArrowPainter(Color color) {
		this.color = color;
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		GeneralPath arrow = new GeneralPath();
		
		g = Stratus.g2d(g);
		
		width = height;
		
		float modX = width/3.5f;
		float modY = height/2.5f;
		
		float midX = width/2f;
		float midY = height/2f;
		float startX = midX - modX;
		float endX = midX + modX;
		float startY = midY - modY;
		float endY = midY + modY;
		

		arrow.moveTo(startX, startY);
		arrow.lineTo(endX, midY);
		arrow.lineTo(startX, endY);
		arrow.closePath();
		g.setPaint(color);
		g.fill(arrow);
		
	}
	
}

