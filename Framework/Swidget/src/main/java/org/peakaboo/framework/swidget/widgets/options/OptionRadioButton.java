package org.peakaboo.framework.swidget.widgets.options;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;

public class OptionRadioButton extends OptionBox {

	public <T> OptionRadioButton(OptionBlock block, ButtonGroup group, String title, String description, T value, Supplier<T> getter, Consumer<T> setter) {
		
		super(block);
		
		JRadioButton selector = new JRadioButton();
		group.add(selector);
		selector.setSelected(value.equals(getter));
		selector.addChangeListener((ChangeEvent e) -> {
			if (!selector.isSelected()) return;
			setter.accept(value);
		});
		
					
		this.add(selector);
		this.addSpacer();
		this.add(new OptionLabel(title, description));
		this.addExpander();
		
	}
	
	public OptionRadioButton(OptionBlock block, ButtonGroup group, String title, String description, boolean selected, Runnable onSelect) {
		
		super(block);
		
		JRadioButton selector = new JRadioButton();
		group.add(selector);
		selector.setSelected(selected);
		selector.addChangeListener((ChangeEvent e) -> {
			if (!selector.isSelected()) return;
			onSelect.run();
		});
		
					
		this.add(selector);
		this.addSpacer();
		this.add(new OptionLabel(title, description));
		this.addExpander();
		
	}
	
}
