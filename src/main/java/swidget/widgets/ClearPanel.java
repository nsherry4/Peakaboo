package swidget.widgets;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JPanel;


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
		super.setToolTipText(ImageButton.getWrappingTooltipText(this, text));
	}

}
