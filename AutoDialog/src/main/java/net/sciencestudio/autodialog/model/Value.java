package net.sciencestudio.autodialog.model;

import eventful.EventfulType;
import net.sciencestudio.autodialog.model.style.Style;

public interface Value<T> {

	Style<T> getStyle();
	String getName();
	
	boolean setValue(T value);
	T getValue();
	
	boolean isEnabled();
	void setEnabled(boolean enabled);

	EventfulType<T> getValueHook();
	EventfulType<Boolean> getEnabledHook();
	
}
