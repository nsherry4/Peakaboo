package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A layout manager that arranges three components in a header-style layout.
 * <p>
 * HeaderLayout positions left and right components at their preferred sizes on the respective
 * edges, while the centre component fills the remaining horizontal space. All components are
 * vertically centred within the container.
 * </p>
 * <p>
 * The layout enforces symmetry by reserving equal space on both sides based on the larger
 * of the two side components. This keeps the centre component visually centred even when
 * left and right components have different widths.
 * </p>
 * <p>
 * <strong>Layout Behaviour:</strong>
 * </p>
 * <ul>
 * <li>Left component: positioned at left edge, vertically centred</li>
 * <li>Centre component: fills horizontal space between reserved side areas</li>
 * <li>Right component: positioned at right edge, vertically centred</li>
 * <li>Side space: both sides reserve max(leftWidth, rightWidth) to maintain symmetry</li>
 * </ul>
 * <p>
 * All components are provided via the constructor and cannot be added or removed dynamically.
 * Any component can be null to leave that section empty.
 * </p>
 *
 * @see HeaderBox
 */
public class HeaderLayout implements LayoutManager2 {

	Component left, centre, right;

	public HeaderLayout(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// NOOP -- All components provided in constructor
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// NOOP -- All components provided in constructor
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension d = new Dimension();
		
		d.width += sideSize();
		d.width += centre == null ? 0 : centre.getPreferredSize().width;
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
		// NOOP -- All components provided in constructor
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
		// NOOP -- Layout does not change over time
	}
	
			
	private int sideSize() {
		int leftWidth = left == null ? 0 : left.getPreferredSize().width;
		int rightWidth = right == null ? 0 : right.getPreferredSize().width;
		return Math.max(leftWidth, rightWidth);
	}

}