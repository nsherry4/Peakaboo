package org.peakaboo.framework.autodialog.model;


import java.io.Serializable;
import java.util.function.Function;

import org.peakaboo.framework.autodialog.model.classinfo.ClassInfo;
import org.peakaboo.framework.autodialog.model.classinfo.ClassInfoDefaults;
import org.peakaboo.framework.autodialog.model.style.Style;

import org.peakaboo.framework.eventful.EventfulType;

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
	
	private ClassInfo<T> 	classInfo;
		
	
	public Parameter(String name, Style<T> style, T value) {
		this(name, style, value, p -> true);
	}

	public Parameter(String name, Style<T> style, T value, ClassInfo<T> classInfo) {
		this(name, style, value, classInfo, p -> true);
	}
	
	public Parameter(String name, Style<T> style, T value, Function<Parameter<T>, Boolean> validator)
	{
		this(name, style, value, null, validator);
	}
	
	public Parameter(String name, Style<T> style, T value, ClassInfo<T> classInfo, Function<Parameter<T>, Boolean> validator)
	{
		this.style = style;
		this.name = name;
		this.enabled = true;
		this.validator = validator;
		
		this.classInfo = classInfo;
		//if class info is null, try to guess it
		if (this.classInfo == null && value == null) {
			throw new UnsupportedOperationException("Cannot create Parameter with no ClassInfo and a null value");
		} else if (this.classInfo == null) {
			//Try to guess the class info for primitives
			this.classInfo = (ClassInfo<T>) ClassInfoDefaults.guess(value.getClass());
		}
		
		//If guessing failed, throw exception
		if (this.classInfo == null) {
			throw new UnsupportedOperationException("Cannot determine ClassInfo information automatically");
		}
		
		
		assignValue(value);
	}

	
	private void assignValue(T value) {
		if (classInfo != null && value != null && !(classInfo.getValueClass().isInstance(value))) {
			throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + classInfo.getValueClass().getName());
		}
		this.value = value;
	}
	
	@Override
	public synchronized boolean setValue(T value) {
		boolean success = true;
		T oldValue = getValue();
		assignValue(value);
		if (!validator.apply(this)) {
			//revert
			assignValue(oldValue);
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
	
	
	public void deserialize(String stored) {
		setValue(classInfo.deserialize(stored));
	}
	
	public String serialize() {
		return classInfo.serialize(getValue());
	}
	
	
}
