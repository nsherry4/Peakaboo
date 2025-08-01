package org.peakaboo.ui.swing.options;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SpinnerNumberModel;

import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionSpinner;

public class OptionThreadCount extends OptionSpinner<Integer> {
	
	public OptionThreadCount(OptionBlock block, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(block, new SpinnerNumberModel(getter.get().intValue(), 1, Math.max(Plural.cores() * 2, 24), 1));
		
		this.withListener(value -> {
			if (!value.equals(getter.get())) {
				setter.accept(value);
			}
		}).withResetButton(Plural::cores);
	}
	
}
