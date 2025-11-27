package org.peakaboo.framework.stratus.components.ui.header;

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

public class HeaderPanelTest extends StratusTest {

	private HeaderPanel headerPanel;

	@Before
	public void setUp() {
		headerPanel = new HeaderPanel();
	}

	@Test
	public void testDefaultConstructor() {
		assertNotNull("HeaderPanel should be created", headerPanel);
		assertNotNull("Header should be created", headerPanel.getHeader());
		assertNotNull("Body should have default JPanel", headerPanel.getBody());
		assertTrue("Default body should be a JPanel", headerPanel.getBody() instanceof JPanel);
	}

	@Test
	public void testConstructorWithHeaderAndBody() {
		HeaderBox customHeader = new HeaderBox(null, "Custom", null);
		JButton customBody = new JButton("Custom Body");

		HeaderPanel panel = new HeaderPanel(customHeader, customBody);

		assertSame("Header should be the custom header", customHeader, panel.getHeader());
		assertSame("Body should be the custom body", customBody, panel.getBody());
	}

	@Test
	public void testGetHeader() {
		HeaderBox header = headerPanel.getHeader();
		assertNotNull("Header should not be null", header);
		assertSame("Should return same header instance", header, headerPanel.getHeader());
	}

	@Test
	public void testSetBody() {
		JButton newBody = new JButton("New Body");
		headerPanel.setBody(newBody);

		assertSame("Body should be updated", newBody, headerPanel.getBody());
	}

	@Test
	public void testSetBodyReplacesOldBody() {
		JButton firstBody = new JButton("First");
		JButton secondBody = new JButton("Second");

		headerPanel.setBody(firstBody);
		assertEquals("First body should be set", firstBody, headerPanel.getBody());

		headerPanel.setBody(secondBody);
		assertEquals("Second body should replace first", secondBody, headerPanel.getBody());
	}

	@Test
	public void testSetBodyWithNull() {
		JButton body = new JButton("Body");
		headerPanel.setBody(body);
		assertNotNull("Body should be set", headerPanel.getBody());

		headerPanel.setBody(null);
		assertNull("Body should be null after setting to null", headerPanel.getBody());
	}

	@Test
	public void testGetBody() {
		JButton body = new JButton("Test");
		headerPanel.setBody(body);

		assertSame("getBody should return the set body", body, headerPanel.getBody());
	}
}