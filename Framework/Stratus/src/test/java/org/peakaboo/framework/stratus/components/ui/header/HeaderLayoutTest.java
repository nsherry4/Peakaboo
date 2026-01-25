package org.peakaboo.framework.stratus.components.ui.header;

import static org.junit.Assert.*;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

public class HeaderLayoutTest {

	private Container container;
	private JButton leftButton;
	private JLabel centreLabel;
	private JButton rightButton;

	@Before
	public void setUp() {
		container = new JPanel();
		leftButton = new JButton("Left");
		centreLabel = new JLabel("Centre");
		rightButton = new JButton("Right");

		leftButton.setPreferredSize(new Dimension(80, 30));
		centreLabel.setPreferredSize(new Dimension(200, 30));
		rightButton.setPreferredSize(new Dimension(60, 30));
	}

	@Test
	public void testPreferredLayoutSizeWithAllComponents() {
		HeaderLayout layout = new HeaderLayout(leftButton, centreLabel, rightButton);

		Dimension preferred = layout.preferredLayoutSize(container);

		// Width should be: max(80, 60) + 200 + max(80, 60) = 80 + 200 + 80 = 360
		assertEquals("Width should account for symmetry", 360, preferred.width);
		// Height should be max of all components
		assertEquals("Height should be max of all components", 30, preferred.height);
	}

	@Test
	public void testPreferredLayoutSizeWithAsymmetricSides() {
		JButton wideLeft = new JButton("Wide Left");
		wideLeft.setPreferredSize(new Dimension(120, 30));

		HeaderLayout layout = new HeaderLayout(wideLeft, centreLabel, rightButton);

		Dimension preferred = layout.preferredLayoutSize(container);

		// Width should be: max(120, 60) + 200 + max(120, 60) = 120 + 200 + 120 = 440
		// This ensures symmetry - both sides get 120 even though right is only 60
		assertEquals("Width should reserve equal space on both sides", 440, preferred.width);
	}

	@Test
	public void testPreferredLayoutSizeWithNullComponents() {
		HeaderLayout layout = new HeaderLayout(null, centreLabel, null);

		Dimension preferred = layout.preferredLayoutSize(container);

		// Width should be just the centre component
		assertEquals("Width should be centre width only", 200, preferred.width);
		assertEquals("Height should be centre height", 30, preferred.height);
	}

	@Test
	public void testPreferredLayoutSizeWithAllNull() {
		HeaderLayout layout = new HeaderLayout(null, null, null);

		Dimension preferred = layout.preferredLayoutSize(container);

		assertEquals("Width should be 0 with all null", 0, preferred.width);
		assertEquals("Height should be 0 with all null", 0, preferred.height);
	}

	@Test
	public void testMinimumLayoutSize() {
		HeaderLayout layout = new HeaderLayout(leftButton, centreLabel, rightButton);

		Dimension minimum = layout.minimumLayoutSize(container);

		// Minimum width should be symmetrical sides only (no centre)
		assertEquals("Minimum width should be symmetric sides", 160, minimum.width);
		assertEquals("Minimum height should be max of all", 30, minimum.height);
	}

	@Test
	public void testLayoutContainerPositioning() {
		container.setSize(400, 40);
		container.setLayout(new HeaderLayout(leftButton, centreLabel, rightButton));
		container.add(leftButton);
		container.add(centreLabel);
		container.add(rightButton);

		container.doLayout();

		// Left should be at x=0
		assertEquals("Left x position should be 0", 0, leftButton.getX());

		// Right should be at the right edge (400 - 60 = 340)
		assertEquals("Right x position should be at right edge", 340, rightButton.getX());

		// Centre should fill the middle space
		// With sideSize=80, centre should start at 80
		assertEquals("Centre x position should start after left reserve", 80, centreLabel.getX());

		// All should be vertically centred (container height 40, component height 30)
		// Delta = (40 - 30) / 2 = 5
		assertEquals("Left should be vertically centred", 5, leftButton.getY());
		assertEquals("Centre should be at top", 0, centreLabel.getY());
		assertEquals("Right should be vertically centred", 5, rightButton.getY());
	}

	@Test
	public void testLayoutContainerWithNullLeft() {
		container.setSize(400, 40);
		container.setLayout(new HeaderLayout(null, centreLabel, rightButton));
		container.add(centreLabel);
		container.add(rightButton);

		container.doLayout();

		// Centre should still be positioned symmetrically based on right size
		assertEquals("Centre should account for right component width", 60, centreLabel.getX());
		assertEquals("Right should be at right edge", 340, rightButton.getX());
	}

	@Test
	public void testMaximumLayoutSize() {
		HeaderLayout layout = new HeaderLayout(leftButton, centreLabel, rightButton);
		Dimension max = layout.maximumLayoutSize(container);

		assertEquals("Maximum width should be Integer.MAX_VALUE", Integer.MAX_VALUE, max.width);
		assertEquals("Maximum height should be Integer.MAX_VALUE", Integer.MAX_VALUE, max.height);
	}
}