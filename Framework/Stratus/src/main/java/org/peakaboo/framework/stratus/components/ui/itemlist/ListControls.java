package org.peakaboo.framework.stratus.components.ui.itemlist;


import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JButton;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ButtonBox;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;



public class ListControls extends ClearPanel {

	private JButton add, remove, clear;
	private ButtonBox box;
	private Map<Component, Function<ElementCount, Boolean>> customButtons = new LinkedHashMap<>();
	
	public enum ElementCount
	{
		NONE, ONE, MANY
	}
	
	public ListControls(FluentButton add, FluentButton remove, FluentButton clear) {
		this.add = add;
		this.remove = remove;
		this.clear = clear;
		
		configureButton(add);
		configureButton(remove);
		configureButton(clear);
		
		
		setElementCount(ElementCount.NONE);
		
		box = new ButtonBox(Spacing.small, false);
		box.addLeft(add);
		box.addLeft(remove);
		box.addRight(clear);
		this.setLayout(new BorderLayout());
		this.add(box, BorderLayout.CENTER);
		this.setBorder(Spacing.bNone());
		
	}
	
	private void configureButton(FluentButton b) {
		b.withBordered(false);
	}
	
	public void setElementCount(int elements) {
		ElementCount ec = ElementCount.NONE;
		if (elements == 0) {
			ec = ElementCount.NONE;
		} else if (elements == 1) {
			ec = ElementCount.ONE;
		} else if (elements >= 2) {
			ec = ElementCount.MANY;
		}
		setElementCount(ec);
	}
	
	public void setElementCount(ElementCount ec)
	{

		switch (ec) {
		case MANY:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		case NONE:
			add.setEnabled(true);
			remove.setEnabled(false);
			clear.setEnabled(false);
			break;
		case ONE:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		default:
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			break;
		}
		
		for (Component button : customButtons.keySet()) {
			boolean enabled = customButtons.get(button).apply(ec);
			button.setEnabled(enabled);
		}

	}
	
	public void addLeft(Component button) {
		this.addLeft(button, e -> true);
	}
	public void addLeft(Component button, Function<ElementCount, Boolean> ec) {
		customButtons.put(button, ec);
		if (button instanceof FluentButton) configureButton((FluentButton) button);
		box.addLeft(button);
	}
	
	
	public void addRight(Component button) {
		this.addLeft(button, e -> true);
	}
	public void addRight(Component button, Function<ElementCount, Boolean> ec) {
		customButtons.put(button, ec);
		if (button instanceof FluentButton) configureButton((FluentButton) button);
		box.addRight(button);
	}
	
	
}
