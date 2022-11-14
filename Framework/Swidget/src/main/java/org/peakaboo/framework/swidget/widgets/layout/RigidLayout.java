package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.peakaboo.framework.swidget.widgets.ClearPanel;

public class RigidLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		//NOOP
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
		// NOOP
	}
	
	@Override
	public void layoutContainer(Container c) {
		//Only one component, and its size is exactly the same as its container 
		if (c.getComponents().length == 0) return;
		Component child = c.getComponents()[0];
		child.setBounds(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	public Dimension minimumLayoutSize(Container c) {
		//Component minimum size is whatever the container's minimum size is
		return c.getMinimumSize();
	}

	@Override
	public Dimension preferredLayoutSize(Container c) {
		//Component preferred size is whatever the container's size is right now
		return c.getSize();
	}

	public static JPanel wrap(Component component) {
		ClearPanel wrapper = new ClearPanel(new RigidLayout());
		wrapper.add(component);
		return wrapper;		
	}


}
