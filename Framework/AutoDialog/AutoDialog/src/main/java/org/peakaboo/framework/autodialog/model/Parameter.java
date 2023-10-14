package org.peakaboo.framework.autodialog.model;


import java.io.Serializable;
import java.util.function.Predicate;

import org.peakaboo.framework.autodialog.model.classinfo.ClassInfo;
import org.peakaboo.framework.autodialog.model.classinfo.ClassInfoDefaults;
import org.peakaboo.framework.autodialog.model.style.Style;

/**
 * 
 * This class defines a parameter -- a {@link Value} with validation and serialization
 * 
 * @author Nathaniel Sherry
 */

public class Parameter<T> extends SimpleValue<T> implements Serializable {

	private Predicate<Parameter<T>> validator;
	private ClassInfo<T> classInfo;
	
	public Parameter(String name, Style<T> style, T value) {
		this(name, style, value, p -> true);
	}

	public Parameter(String name, Style<T> style, T value, ClassInfo<T> classInfo) {
		this(name, style, value, classInfo, p -> true);
	}
	
	public Parameter(String name, Style<T> style, T value, Predicate<Parameter<T>> validator) {
		this(name, style, value, null, validator);
	}
	
	public Parameter(String name, Style<T> style, T value, ClassInfo<T> classInfo, Predicate<Parameter<T>> validator) {
		super(name, style, value);
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

	// 
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
		if (!validator.test(this)) {
			//revert
			assignValue(oldValue);
			success = false;
		}
		//notify value listeners. Even if validation failed, so that the failing editor can revert.
		getValueHook().updateListeners(value);
		return success;
	}


	// Not for serialization
	public String toString() {
		String str =  "Parameter: " + getName();
		if (value != null) str += ": " + value.toString();
		return str;
	}


	public Predicate<Parameter<T>> getValidator() {
		return validator;
	}
	
	
	public void deserialize(String stored) {
		setValue(classInfo.deserialize(stored));
	}
	
	public String serialize() {
		return classInfo.serialize(getValue());
	}
	
	
}
