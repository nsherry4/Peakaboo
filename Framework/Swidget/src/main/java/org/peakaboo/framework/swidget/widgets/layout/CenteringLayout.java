package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager2;

/**
 * Centres a single component without expanding it like BorderLayout would.
 * 
 * @author NAS
 *
 */
public class CenteringLayout implements LayoutManager2 {

	private Component component;
	private GridBagLayout backer = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();

	@Override
	public void addLayoutComponent(String name, Component comp) {
		if (component != null) {
			return;
		}

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0f;
		c.weighty = 0f;

		backer.addLayoutComponent(comp, c);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		if (comp == component) {
			component = null;
			backer.removeLayoutComponent(comp);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return backer.preferredLayoutSize(parent);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return backer.minimumLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		backer.layoutContainer(parent);
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (component != null) {
			return;
		}

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0f;
		c.weighty = 0f;

		backer.addLayoutComponent(comp, c);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return backer.maximumLayoutSize(target);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return backer.getLayoutAlignmentX(target);
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return backer.getLayoutAlignmentY(target);
	}

	@Override
	public void invalidateLayout(Container target) {
		backer.invalidateLayout(target);
	}

}