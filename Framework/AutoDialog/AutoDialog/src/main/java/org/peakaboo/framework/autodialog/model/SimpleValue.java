package org.peakaboo.framework.autodialog.model;

import org.peakaboo.framework.autodialog.model.style.Style;
import org.peakaboo.framework.eventful.EventfulType;

public class SimpleValue<T> implements Value<T> {

	// Internal state of this value
	protected Style<T> style;
	protected String name;
	protected T value;
	protected boolean enabled;
	
	// Used to emit events to listeners
	protected EventfulType<T> valueHook;
	protected EventfulType<Boolean> enabledHook;
	
	public SimpleValue(String name, Style<T> style, T value) {
		if (name == null) throw new IllegalArgumentException("Name cannot be null");
		if (style == null) throw new IllegalArgumentException("Name cannot be null");
		
		this.name = name;
		this.style = style;
		this.value = value;
		this.enabled = true;
		valueHook = new EventfulType<>();
		enabledHook = new EventfulType<>();
	}
	
	@Override
	public synchronized T getValue() {
		return value;
	}
	
	@Override
	public boolean setValue(T value) {
		this.value = value;
		getValueHook().updateListeners(value);
		return true;
	}

	
	@Override
	public Style<T> getStyle() {
		return style;
	}

	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		getEnabledHook().updateListeners(enabled);
	}
	


	@Override
	public EventfulType<T> getValueHook() {
		return valueHook;
	}

	@Override
	public EventfulType<Boolean> getEnabledHook() {
		return enabledHook;
	}


	
}
