package org.peakaboo.framework.autodialog.model;

import org.peakaboo.framework.autodialog.AutoDialog;
import org.peakaboo.framework.autodialog.model.style.Style;
import org.peakaboo.framework.eventful.EventfulType;

public interface Value<T> {

	Style<T> getStyle();
	String getName();
	default String getSlug() {
		return AutoDialog.sluggify(getName());
	}
	
	boolean setValue(T value);
	T getValue();
	
	boolean isEnabled();
	void setEnabled(boolean enabled);

	EventfulType<T> getValueHook();
	EventfulType<Boolean> getEnabledHook();
	
}
