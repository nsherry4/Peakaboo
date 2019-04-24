package org.peakaboo.framework.swidget.widgets;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;


public class ClearPanel extends JPanel
{

	public ClearPanel()
	{
		setOpaque(false);
	}


	public ClearPanel(LayoutManager arg0)
	{
		super(arg0);
		setOpaque(false);
	}


	public ClearPanel(boolean arg0)
	{
		super(arg0);
		setOpaque(false);
	}


	public ClearPanel(LayoutManager arg0, boolean arg1)
	{
		super(arg0, arg1);
		setOpaque(false);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		Component[] components = getComponents();
		if (components != null && components.length > 0) 
		{
			int count = components.length;
			for (int i = 0; i < count; i++)
			components[i].setEnabled(enabled);
		}
	}
	
	@Override
	public void setToolTipText(String text)
	{
		super.setToolTipText(Swidget.lineWrapHTML(this, text));
	}

}
