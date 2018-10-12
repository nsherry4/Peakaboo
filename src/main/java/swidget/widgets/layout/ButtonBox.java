package swidget.widgets.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;


public class ButtonBox extends JPanel
{
	
	private JPanel left, right, centre;
	private List<Component> ll, rl, cl;
	
	private JPanel buttonPanel;
		
	public ButtonBox()
	{
		this(Spacing.huge, true);
	}
	public ButtonBox(boolean divider)
	{
		this(Spacing.huge, divider);
	}
	public ButtonBox(int spacing, boolean divider)
	{
				
		setLayout(new BorderLayout());
		
		buttonPanel = new ClearPanel();		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		if (divider) add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
		
		ll = new ArrayList<Component>();
		cl = new ArrayList<Component>();
		rl = new ArrayList<Component>();
		
		left = new ClearPanel();
		centre = new ClearPanel();
		right = new ClearPanel();
					
		left.setLayout(new FlowLayout(FlowLayout.LEFT, spacing, spacing));
		centre.setLayout(new FlowLayout(FlowLayout.CENTER, spacing, spacing));
		right.setLayout(new FlowLayout(FlowLayout.RIGHT, spacing, spacing));
		
		buttonPanel.add(left);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(centre);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(right);
		
		
		
		
	}
	
	private void relayout()
	{
		
		centre.removeAll();
		for (int i = 0; i < cl.size(); i++)
		{
			Component c = cl.get(i);
			centre.add(c);
		}
		
		left.removeAll();
		for (int i = 0; i < ll.size(); i++)
		{
			Component c = ll.get(i);
			left.add(c);
		}
		
		right.removeAll();
		for (int i = 0; i < rl.size(); i++)
		{
			Component c = rl.get(i);
			right.add(c);
		}
		
	}
	
	
	public void addLeft(int index, Component c)
	{
		ll.add(index, c);
		relayout();
	}
	public void addLeft(Component c)
	{
		ll.add(c);
		relayout();
	}
	
	public void addRight(int index, Component c)
	{
		rl.add(index, c);
		relayout();
	}
	public void addRight(Component c)
	{
		rl.add(c);
		relayout();
	}
	
	public void addCentre(int index, Component c)
	{
		cl.add(index, c);
		relayout();
	}
	public void addCentre(Component c)
	{
		cl.add(c);
		relayout();
	}

}
