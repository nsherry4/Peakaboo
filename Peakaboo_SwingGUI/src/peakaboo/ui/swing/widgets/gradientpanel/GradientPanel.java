package peakaboo.ui.swing.widgets.gradientpanel;


import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;


import javax.swing.UIManager;

import peakaboo.ui.swing.widgets.ClearPanel;


public class GradientPanel extends ClearPanel
{
	
	private boolean drawBackground;

	public GradientPanel(boolean drawBackground)
	{
		super();
		this.drawBackground = drawBackground;
	}
	public GradientPanel()
	{
		super();
		this.drawBackground = false;
	}


	@Override
	public void paintComponent(Graphics g)
	{
		
		if (drawBackground){
			Graphics2D g2 = (Graphics2D) g;
	
			g2.setColor(UIManager.getColor("control"));
			g2.fillRect(0, 0, getWidth(), getHeight());
	
			GradientPaint p = new GradientPaint(0f, 0f, new Color(0f, 0f, 0f, 0.05f), 0f, getHeight(), new Color(0f, 0f,
					0f, 0.15f));
			g2.setPaint(p);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		super.paintComponent(g);
		

	}


	public static boolean usingNimbus()
	{
		return UIManager.getLookAndFeel().getName().equals("Nimbus");
	}


}
