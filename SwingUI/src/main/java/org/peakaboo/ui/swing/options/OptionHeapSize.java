package org.peakaboo.ui.swing.options;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;

public class OptionHeapSize extends OptionRadioButton {

	private JTextField size;
	private Consumer<Integer> setter;
	
	public OptionHeapSize(OptionBlock block, ButtonGroup group, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(block, group);
		this.setter = setter;
		
		size = new JTextField(getter.get() + "", 6);
		size.setMaximumSize(size.getPreferredSize());
		this.add(size);
		
		//Listeners for the size textbox
		size.addActionListener(e -> {
			set();
		});
		size.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				this.update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				this.update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				this.update();
			}
			
			private void update() {
				if (size.getText().length() == 0) { return;	}
				set();
			}
			
		});
		
	}
	

	private void set() {
		try {
			int value = Integer.parseInt(size.getText());
			setter.accept(value);
		} catch (NumberFormatException e) {
			
		}
	}
	
	
	
}
