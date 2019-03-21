package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;

import stratus.controls.ButtonLinker;

public class HeaderTabBuilder {

	private JPanel body;
	private ButtonGroup group;
	private ButtonLinker linker;
	private CardLayout layout;
	
	public HeaderTabBuilder() {
		layout = new CardLayout();
		body = new JPanel(layout);
		group = new ButtonGroup();
		linker = new ButtonLinker();
	}
	
	public void addTab(String title, Component component) {
		body.add(component, title);
		ToggleImageButton button = new ToggleImageButton(title).withAction(() -> {
			layout.show(body, title);
		});
		group.add(button);
		linker.addButton(button);
		
		//first tab is selected
		if (group.getButtonCount() == 1) {
			button.setSelected(true);
			layout.show(body, title);
		}
		
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
	
}
