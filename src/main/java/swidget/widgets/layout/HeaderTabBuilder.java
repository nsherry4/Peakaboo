package swidget.widgets.layout;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import stratus.controls.ToggleButtonLinker;
import swidget.widgets.buttons.ToggleImageButton;

public class HeaderTabBuilder {

	private JPanel body;
	private ButtonGroup group;
	private ToggleButtonLinker linker;
	private CardLayout layout;
	
	public HeaderTabBuilder() {
		layout = new CardLayout();
		body = new JPanel(layout);
		group = new ButtonGroup();
		linker = new ToggleButtonLinker();
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
	
	public ToggleButtonLinker getTabStrip() {
		return linker;
	}
	
	public ButtonGroup getButtonGroup() {
		return group;
	}
	
}
