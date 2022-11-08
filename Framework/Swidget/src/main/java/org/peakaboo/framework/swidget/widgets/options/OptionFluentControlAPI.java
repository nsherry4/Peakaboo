package org.peakaboo.framework.swidget.widgets.options;

import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

public interface OptionFluentControlAPI<T> extends OptionFluentAPI<T> {

	public OptionFluentAPI<T> withListener(Consumer<T> event);
	public OptionFluentAPI<T> withKeyStroke(KeyStroke keystroke, JComponent parent);
	
}
