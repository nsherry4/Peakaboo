package org.peakaboo.framework.stratus.components.ui.header;

import static org.junit.Assert.*;

import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;

public class HeaderTabBuilderTest {

	private HeaderTabBuilder builder;

	@Before
	public void setUp() {
		builder = new HeaderTabBuilder();
	}

	@Test
	public void testConstructor() {
		assertNotNull("Builder should be created", builder);
		assertNotNull("Body panel should be created", builder.getBody());
		assertNotNull("Button group should be created", builder.getButtonGroup());
		assertNotNull("Tab strip should be created", builder.getTabStrip());
		assertNotNull("Buttons map should be created", builder.getButtons());
		assertTrue("Buttons map should be empty initially", builder.getButtons().isEmpty());
	}

	@Test
	public void testAddTab() {
		JPanel panel1 = new JPanel();
		FluentToggleButton button = builder.addTab("Tab 1", panel1);

		assertNotNull("Button should be returned", button);
		assertEquals("Button text should match tab title", "Tab 1", button.getText());
		assertTrue("First tab should be selected", button.isSelected());
		assertEquals("Button group should have 1 button", 1, builder.getButtonGroup().getButtonCount());
	}

	@Test
	public void testAddMultipleTabs() {
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();

		FluentToggleButton button1 = builder.addTab("Tab 1", panel1);
		FluentToggleButton button2 = builder.addTab("Tab 2", panel2);
		FluentToggleButton button3 = builder.addTab("Tab 3", panel3);

		assertEquals("Button group should have 3 buttons", 3, builder.getButtonGroup().getButtonCount());
		assertTrue("First tab should be selected", button1.isSelected());
		assertFalse("Second tab should not be selected", button2.isSelected());
		assertFalse("Third tab should not be selected", button3.isSelected());
	}

	@Test
	public void testWithTabFluentAPI() {
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		HeaderTabBuilder result = builder
			.withTab("Tab 1", panel1)
			.withTab("Tab 2", panel2);

		assertSame("withTab should return this for chaining", builder, result);
		assertEquals("Should have 2 tabs", 2, builder.getButtons().size());
	}

	@Test
	public void testGetButtons() {
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		builder.addTab("Tab 1", panel1);
		builder.addTab("Tab 2", panel2);

		Map<String, FluentToggleButton> buttons = builder.getButtons();

		assertEquals("Should have 2 buttons in map", 2, buttons.size());
		assertTrue("Should contain Tab 1", buttons.containsKey("Tab 1"));
		assertTrue("Should contain Tab 2", buttons.containsKey("Tab 2"));
	}

	@Test
	public void testGetButtonsReturnsUnmodifiableCopy() {
		JPanel panel1 = new JPanel();
		builder.addTab("Tab 1", panel1);

		Map<String, FluentToggleButton> buttons = builder.getButtons();

		try {
			buttons.put("Tab 2", new FluentToggleButton("Fake"));
			fail("Should not be able to modify returned map");
		} catch (UnsupportedOperationException e) {
			// Expected - map should be unmodifiable
		}
	}

	@Test
	public void testGetTabStrip() {
		ButtonLinker linker = builder.getTabStrip();
		assertNotNull("Tab strip should not be null", linker);
		assertSame("Should return same linker instance", linker, builder.getTabStrip());
	}

	@Test
	public void testGetBody() {
		JPanel body = builder.getBody();
		assertNotNull("Body should not be null", body);
		assertSame("Should return same body instance", body, builder.getBody());
	}

	@Test
	public void testGetButtonGroup() {
		ButtonGroup group = builder.getButtonGroup();
		assertNotNull("Button group should not be null", group);
		assertSame("Should return same group instance", group, builder.getButtonGroup());
	}
}