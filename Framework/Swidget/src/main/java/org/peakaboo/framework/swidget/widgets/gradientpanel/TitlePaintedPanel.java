package org.peakaboo.framework.swidget.widgets.gradientpanel;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.peakaboo.framework.swidget.widgets.Spacing;



public class TitlePaintedPanel extends PaintedPanel
{

	public enum Side
	{
		TOP, BOTTOM, LEFT, RIGHT;
	}

	public TitlePaintedPanel(String title, boolean drawBackground, JComponent bottomWidget){
		super(drawBackground);
		init(title, bottomWidget, Side.BOTTOM);
	}
	
	public TitlePaintedPanel(String title, boolean drawBackground, JComponent widget, TitlePaintedPanel.Side side){
		super(drawBackground);
		init(title, widget, side);
	}
	
	public TitlePaintedPanel(String title, boolean drawBackground){
		super(drawBackground);
		init(title, null, null);
	}
	public TitlePaintedPanel(String title){
		super(false);
		init(title, null, null);
	}
	
	private void init(String title, JComponent widget, TitlePaintedPanel.Side widgetSide){
		
		setLayout(new BorderLayout());
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setOpaque(false);
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize() + 4f).deriveFont(Font.BOLD));
		titleLabel.setBorder(Spacing.bMedium());
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		
		add(titleLabel, BorderLayout.CENTER);
		
		addSideComponent(widget, widgetSide);
	}
	
	
	public void addSideComponent(JComponent widget, Side widgetSide)
	{
		if (widget != null)
		{
			
			String side;
			switch (widgetSide)
			{
				case TOP:
					side = BorderLayout.NORTH;
					break;
				case BOTTOM:
					side = BorderLayout.SOUTH;
					break;
				case LEFT:
					side = BorderLayout.WEST;
					break;
				case RIGHT:
					side = BorderLayout.EAST;
					break;
				default:
					side = BorderLayout.SOUTH;
			}
			
			add(widget, side);
		}
	}
	
	
}
