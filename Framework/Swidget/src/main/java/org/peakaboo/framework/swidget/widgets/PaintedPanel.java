package org.peakaboo.framework.swidget.widgets;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.UIManager;



public class PaintedPanel extends ClearPanel
{
	
	private boolean drawBackground;
	private Paint backgroundPaint = null;
	
	public PaintedPanel(boolean drawBackground)
	{
		super();
		this.drawBackground = drawBackground;
	}
	public PaintedPanel()
	{
		this(true);
	}

	
	
	

	public boolean isDrawBackground() {
		return drawBackground;
	}
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		this.repaint();
	}
	public Paint getBackgroundPaint() {
		return backgroundPaint;
	}
	public void setBackgroundPaint(Paint backgroundPaint) {
		this.backgroundPaint = backgroundPaint;
	}
	public PaintedPanel backgroundPaint(Paint backgroundPaint) {
		setBackgroundPaint(backgroundPaint);
		return this;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		
		if (drawBackground){
			g = g.create();
			Graphics2D g2 = (Graphics2D) g;
			
			Paint paint = getBackgroundPaint();
			if (paint != null) {
				g2.setPaint(paint);
			} else {
				g2.setColor(UIManager.getColor("control"));

			}
			g2.fillRect(0, 0, getWidth(), getHeight());

		}

		super.paintComponent(g);
		

	}



}
