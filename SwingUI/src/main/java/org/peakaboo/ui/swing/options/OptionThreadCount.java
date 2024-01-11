package org.peakaboo.ui.swing.options;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionCustomComponent;

public class OptionThreadCount extends OptionCustomComponent {

	private JSpinner spinner;
	private Consumer<Integer> setter;
	private Supplier<Integer> getter;
	
	public OptionThreadCount(OptionBlock block, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(block, new JSpinner(new SpinnerNumberModel((int)getter.get(), 1, (int)Math.max(Plural.cores() * 2, 24), 1)), false);
		this.setter = setter;
		this.getter = getter;
		
		spinner = (JSpinner) super.getComponent();
		spinner.setMaximumSize(spinner.getPreferredSize());
		
		//Listeners for the size textbox
		spinner.addChangeListener(change -> {
			set();
		});
		
	}
	

	private void set() {
		try {
			int value = (int)spinner.getValue();
			if (value != getter.get()) {
				setter.accept(value);
			}
		} catch (NumberFormatException e) {
			
		}
	}
	
	
	
}
