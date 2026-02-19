package org.peakaboo.framework.stratus.components.ui.header;

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;

public class HeaderBoxTest extends StratusTest {

	private HeaderBox headerBox;

	@Before
	public void setUp() {
		headerBox = new HeaderBox();
	}

	@Test
	public void testDefaultConstructor() {
		assertNotNull("HeaderBox should be created", headerBox);
		assertNull("Left should be null by default", headerBox.getLeft());
		assertNotNull("Centre should not be null (empty string title)", headerBox.getCentre());
		assertNull("Right should be null by default", headerBox.getRight());
		assertFalse("ShowClose should be false by default", headerBox.getShowClose());
	}

	@Test
	public void testConstructorWithComponents() {
		JButton left = new JButton("Left");
		JButton right = new JButton("Right");
		HeaderBox box = new HeaderBox(left, "Title", right);

		assertEquals("Left component should be set", left, box.getLeft());
		assertNotNull("Centre component should be created", box.getCentre());
		assertTrue("Centre should be a JLabel", box.getCentre() instanceof JLabel);
		assertEquals("Right component should be set", right, box.getRight());
	}

	@Test
	public void testSetLeft() {
		JButton button = new JButton("Test");
		headerBox.setLeft(button);
		assertEquals("Left component should be updated", button, headerBox.getLeft());
	}

	@Test
	public void testSetCentreWithString() {
		headerBox.setCentre("Test Title");
		assertNotNull("Centre should not be null", headerBox.getCentre());
		assertTrue("Centre should be a JLabel", headerBox.getCentre() instanceof JLabel);
		assertEquals("Centre text should match", "Test Title", ((JLabel) headerBox.getCentre()).getText());
	}

	@Test
	public void testSetCentreWithComponent() {
		JButton button = new JButton("Centre Button");
		headerBox.setCentre(button);
		assertEquals("Centre component should be set", button, headerBox.getCentre());
	}

	@Test
	public void testSetRight() {
		JButton button = new JButton("Test");
		headerBox.setRight(button);
		assertEquals("Right component should be updated", button, headerBox.getRight());
	}

	@Test
	public void testSetComponents() {
		JButton left = new JButton("Left");
		JButton centre = new JButton("Centre");
		JButton right = new JButton("Right");

		headerBox.setComponents(left, centre, right);

		assertEquals("Left should be updated", left, headerBox.getLeft());
		assertEquals("Centre should be updated", centre, headerBox.getCentre());
		assertEquals("Right should be updated", right, headerBox.getRight());
	}

	@Test
	public void testSetComponentsWithStringCentre() {
		JButton left = new JButton("Left");
		JButton right = new JButton("Right");

		headerBox.setComponents(left, "Title", right);

		assertEquals("Left should be updated", left, headerBox.getLeft());
		assertTrue("Centre should be a JLabel", headerBox.getCentre() instanceof JLabel);
		assertEquals("Right should be updated", right, headerBox.getRight());
	}

	@Test
	public void testShowClose() {
		assertFalse("ShowClose should be false initially", headerBox.getShowClose());

		headerBox.setShowClose(true);
		assertTrue("ShowClose should be true after setting", headerBox.getShowClose());

		headerBox.setShowClose(false);
		assertFalse("ShowClose should be false after unsetting", headerBox.getShowClose());
	}

	@Test
	public void testDraggable() {
		assertTrue("Draggable should be true by default", headerBox.isDraggable());

		headerBox.setDraggable(false);
		assertFalse("Draggable should be false after unsetting", headerBox.isDraggable());

		headerBox.setDraggable(true);
		assertTrue("Draggable should be true after setting", headerBox.isDraggable());
	}

	@Test
	public void testSetOnClose() {
		final boolean[] called = {false};
		Runnable onClose = () -> called[0] = true;

		headerBox.setOnClose(onClose);
		// Cannot easily test if it's actually called without triggering UI events
		// but we can verify it doesn't throw
	}

	@Test
	public void testSetOnCloseWithNull() {
		// Should not throw - null is handled
		headerBox.setOnClose(null);
	}

	@Test
	public void testCreateYesNo() {
		final boolean[] yesClicked = {false};
		final boolean[] noClicked = {false};

		HeaderBox box = HeaderBox.createYesNo(
			"Confirm Action",
			"Yes", () -> yesClicked[0] = true,
			"No", () -> noClicked[0] = true
		);

		assertNotNull("HeaderBox should be created", box);
		assertNotNull("Left component should be set (No button)", box.getLeft());
		assertNotNull("Centre component should be set (title)", box.getCentre());
		assertNotNull("Right component should be set (Yes button)", box.getRight());
	}

	@Test
	public void testEnsureBuilt() {
		// ensureBuilt is package-private, but we can test it indirectly
		// by verifying that setting components and then calling ensureBuilt doesn't break
		headerBox.setLeft(new JButton("Test"));
		headerBox.ensureBuilt();
		assertNotNull("Left should still be set", headerBox.getLeft());
	}
}