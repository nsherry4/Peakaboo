package org.peakaboo.framework.swidget.widgets.options;

import java.util.function.Consumer;

import javax.swing.JCheckBox;

public class OptionCheckBox extends OptionBox {
	
	public OptionCheckBox(OptionBlock block, String title, String description, boolean selected, Consumer<Boolean> onChange) {

		super(block);
		
		JCheckBox check = new JCheckBox();
		check.setSelected(selected);
		check.addActionListener(e -> onChange.accept(check.isSelected()));
		
		this.add(check);
		this.addSpacer();
		this.add(new OptionLabel(title, description));
		this.addExpander();
		
		
	}

}
