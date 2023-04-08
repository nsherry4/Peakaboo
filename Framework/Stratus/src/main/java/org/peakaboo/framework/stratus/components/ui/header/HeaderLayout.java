package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class HeaderLayout implements LayoutManager2 {

	Component left, centre, right;

	public HeaderLayout(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension d = new Dimension();
		
		d.width += sideSize();
		d.width += centre.getPreferredSize().width;
		d.width += sideSize();
		
		d.height = left == null ? 0 : left.getPreferredSize().height;
		d.height = Math.max(d.height, centre == null ? 0 : centre.getPreferredSize().height);
		d.height = Math.max(d.height, right == null ? 0 : right.getPreferredSize().height);
		
		return d;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension d = new Dimension();
		
		d.width += sideSize();
		d.width += sideSize();
		
		d.height = left == null ? 0 : left.getPreferredSize().height;
		d.height = Math.max(d.height, centre == null ? 0 : centre.getPreferredSize().height);
		d.height = Math.max(d.height, right == null ? 0 : right.getPreferredSize().height);
		
		return d;
	}

	@Override
	public void layoutContainer(Container parent) {
		Dimension leftSize = left == null ? new Dimension(0, 0) : new Dimension(left.getPreferredSize());
		Dimension rightSize = right == null ? new Dimension(0, 0) : new Dimension(right.getPreferredSize());
		
		float lHalfDelta = Math.max(0, parent.getHeight() - leftSize.height) / 2f;
		float rHalfDelta = Math.max(0, parent.getHeight() - rightSize.height) / 2f;
		
		if (left != null)   left.setBounds(0, (int)lHalfDelta, leftSize.width, leftSize.height);
		if (centre != null) centre.setBounds(sideSize(), 0, parent.getWidth() - sideSize() - sideSize(), parent.getHeight());
		if (right != null)  right.setBounds(parent.getWidth() - rightSize.width, (int)rHalfDelta, rightSize.width, rightSize.height);

	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		// TODO Auto-generated method stub
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {

	}
	
			
	private int sideSize() {
		int leftWidth = left == null ? 0 : left.getPreferredSize().width;
		int rightWidth = right == null ? 0 : right.getPreferredSize().width;
		return Math.max(leftWidth, rightWidth);
	}

}