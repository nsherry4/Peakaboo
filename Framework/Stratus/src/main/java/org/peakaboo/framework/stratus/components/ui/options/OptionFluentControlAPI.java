package org.peakaboo.framework.stratus.components.ui.options;

import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

public interface OptionFluentControlAPI<T> extends OptionFluentAPI {

	public OptionFluentControlAPI<T> withListener(Consumer<T> event);
	public OptionFluentControlAPI<T> withKeyStroke(KeyStroke keystroke, JComponent parent);
	
}
