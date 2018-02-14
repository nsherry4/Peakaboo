package net.sciencestudio.autodialog.model;


import java.io.Serializable;
import java.util.function.Function;

import eventful.EventfulType;
import net.sciencestudio.autodialog.model.style.Style;

/**
 * 
 * This class defines a parameter for a filter.
 * 
 * @author Nathaniel Sherry, 2009-2012
 */

public class Parameter<T> implements Serializable, Value<T>
{
	
	private String			name;
	private Style<T>		style;	
	private boolean			enabled;

	private T				value;
	private Function<Parameter<T>, Boolean> validator;
	
	private EventfulType<T>	valueHook = new EventfulType<>();
	private EventfulType<Boolean> enabledHook = new EventfulType<>();
		
	
	public Parameter(String name, Style<T> style, T value) {
		this(name, style, value, p -> true);
	}
	
	public Parameter(String name, Style<T> style, T value, Function<Parameter<T>, Boolean> validator)
	{
		this.style = style;
		this.name = name;
		this.value = value;
		this.enabled = true;
		this.validator = validator;
	}

	
	@Override
	public synchronized boolean setValue(T value) {
		boolean success = true;
		T oldValue = this.value;
		this.value = value;
		if (!validator.apply(this)) {
			//revert
			this.value = oldValue;
			success = false;
		}
		//notify value listeners. Even if validation failed, so that the failing editor can revert.
		getValueHook().updateListeners(value);
		return success;
	}
	
	@Override
	public synchronized T getValue() {
		return value;
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
	
	public String toString()
	{
		String str =  "Parameter: " + getName();
		if (value != null) str += ": " + value.toString();
		return str;
	}

	public EventfulType<T> getValueHook() {
		return valueHook;
	}

	public EventfulType<Boolean> getEnabledHook() {
		return enabledHook;
	}

	public Function<Parameter<T>, Boolean> getValidator() {
		return validator;
	}
	
	
	
}
