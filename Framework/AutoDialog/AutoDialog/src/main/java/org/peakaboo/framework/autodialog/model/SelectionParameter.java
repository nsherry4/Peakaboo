package org.peakaboo.framework.autodialog.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.peakaboo.framework.autodialog.model.classinfo.ClassInfo;
import org.peakaboo.framework.autodialog.model.style.Style;

public class SelectionParameter<T> extends Parameter<T> {

	private List<T> possibleValues = new ArrayList<>();
	
	public SelectionParameter(String name, Style<T> style, T value)
	{
		super(name, style, value);
	}

	public SelectionParameter(String name, Style<T> style, T value, ClassInfo<T> classInfo)
	{
		super(name, style, value, classInfo);
	}

	public SelectionParameter(String name, Style<T> style, T value, Predicate<Parameter<T>> validator)
	{
		super(name, style, value, validator);
	}
	
	public SelectionParameter(String name, Style<T> style, T value, ClassInfo<T> classInfo, Predicate<Parameter<T>> validator)
	{
		super(name, style, value, classInfo, validator);
	}
	

	public List<T> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(T... possibleValues) {
		setPossibleValues(new ArrayList<>(Arrays.asList(possibleValues)));
	}
	
	public void setPossibleValues(Collection<T> possibleValues) {
		this.possibleValues = new ArrayList<>(possibleValues);
	}
	
	
	
}
