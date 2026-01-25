package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;

/**
 * A builder for creating tabbed interfaces with linked toggle buttons and card layout panels.
 * <p>
 * HeaderTabBuilder simplifies creating tab strips (typically used in {@link HeaderBox} headers)
 * that control which panel is displayed in a body area. It manages the button group, card layout,
 * and button linking automatically.
 * </p>
 * <p>
 * <strong>Usage Pattern:</strong>
 * </p>
 * <pre>
 * HeaderTabBuilder tabs = new HeaderTabBuilder()
 *     .withTab("General", generalPanel)
 *     .withTab("Advanced", advancedPanel);
 *
 * headerBox.setLeft(tabs.getTabStrip());
 * headerPanel.setBody(tabs.getBody());
 * </pre>
 * <p>
 * <strong>Features:</strong>
 * </p>
 * <ul>
 * <li>Automatic button grouping (only one tab selected at a time)</li>
 * <li>Card layout management (shows panel for selected tab)</li>
 * <li>Button linking for visual grouping via {@link ButtonLinker}</li>
 * <li>First tab automatically selected on creation</li>
 * <li>Fluent API via {@link #withTab(String, Component)}</li>
 * <li>Direct button access via {@link #addTab(String, Component)} for customization</li>
 * </ul>
 *
 * @see HeaderBox
 * @see ButtonLinker
 */
public class HeaderTabBuilder {

	private JPanel body;
	private ButtonGroup group;
	private ButtonLinker linker;
	private CardLayout layout;
	
	private Map<String, FluentToggleButton> buttons = new LinkedHashMap<>();
	
	public HeaderTabBuilder() {
		layout = new CardLayout();
		body = new JPanel(layout);
		group = new ButtonGroup();
		linker = new ButtonLinker();
	}
	
	public FluentToggleButton addTab(String title, Component component) {
		body.add(component, title);
		FluentToggleButton button = new FluentToggleButton(title)
				.withAction(() -> layout.show(body, title));
		button.setFont(button.getFont().deriveFont(Font.BOLD));
		buttons.put(title, button);
		group.add(button);
		linker.addButton(button);
		
		//first tab is selected
		if (group.getButtonCount() == 1) {
			button.setSelected(true);
			layout.show(body, title);
		}
		
		return button;
		
	}
	
	public HeaderTabBuilder withTab(String title, Component component) {
		addTab(title, component);
		return this;
	}
	
	public JPanel getBody() {
		return body;
	}
	
	public ButtonLinker getTabStrip() {
		return linker;
	}
	
	public ButtonGroup getButtonGroup() {
		return group;
	}
	
	public Map<String, FluentToggleButton> getButtons() {
		return Map.copyOf(buttons);
	}
	
}
