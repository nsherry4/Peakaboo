package org.peakaboo.framework.stratus.components.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class RigidLayout implements LayoutManager {

	private int width, height;
	
	public RigidLayout(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
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
		if (c.isMinimumSizeSet()) {
			return c.getMinimumSize();
		} else {
			return new Dimension(width, height);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container c) {
		//Component preferred size is whatever the container's size is right now
		if (c.isPreferredSizeSet()) {
			return c.getSize();
		} else {
			return new Dimension(width, height);
		}
	}

	public static JPanel wrap(Component component, int width, int height) {
		ClearPanel wrapper = new ClearPanel(new RigidLayout(width, height));
		wrapper.add(component);
		return wrapper;		
	}


}
