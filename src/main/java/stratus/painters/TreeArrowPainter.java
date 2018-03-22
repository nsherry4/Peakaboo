package stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.Painter;

public class TreeArrowPainter implements Painter<JComponent> {

	private Color color;
	private boolean expanded;
	public TreeArrowPainter(Color color, boolean expanded) {
		this.color = color;
		this.expanded = expanded;
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		GeneralPath arrow = new GeneralPath();
		
		width = height;
		
		float modX = width/2f;
		float modY = height/2f;
		
		float midX = width/2f;
		float midY = height/2f;
		float startX = midX - modX;
		float endX = midX + modX;
		float startY = midY - modY;
		float endY = midY + modY;
		
		if (expanded) {
			arrow.moveTo(startX, startY);
			arrow.lineTo(endX, startY);
			arrow.lineTo(midX, endY);
		} else {
			arrow.moveTo(startX, startY);
			arrow.lineTo(endX, midY);
			arrow.lineTo(startX, endY);
		}
		arrow.closePath();
		g.setPaint(color);
		g.fill(arrow);
		
	}
	
}

