package org.peakaboo.framework.swidget.widgets.gradientpanel;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.UIManager;

import org.peakaboo.framework.swidget.widgets.ClearPanel;



public class PaintedPanel extends ClearPanel
{
	
	private boolean drawBackground;
	private Paint backgroundPaint = new Color(0f, 0f, 0f, 0.1f);
	
	public PaintedPanel(boolean drawBackground)
	{
		super();
		this.drawBackground = drawBackground;
	}
	public PaintedPanel()
	{
		this(true);
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
			
			g2.setColor(UIManager.getColor("control"));
			g2.fillRect(0, 0, getWidth(), getHeight());

			g2.setPaint(backgroundPaint);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		super.paintComponent(g);
		

	}



}
