package org.peakaboo.framework.autodialog.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.framework.autodialog.model.style.Style;
import org.peakaboo.framework.autodialog.model.style.layouts.ColumnLayoutStyle;
import org.peakaboo.framework.eventful.EventfulType;

public class Group implements Value<List<Value<?>>> {

	private List<Value<?>> values = new ArrayList<>();
	private Style<List<Value<?>>> style;
	private String name;
	private boolean enabled = true; 
	
	private EventfulType<List<Value<?>>> valueHook = new EventfulType<>();
	private EventfulType<Boolean> enabledHook = new EventfulType<>();

	public Group(String name) {
		this(name, new ArrayList<>(), new ColumnLayoutStyle());
	}

	public Group(String name, Style<List<Value<?>>> style) {
		this(name, new ArrayList<>(), style);
	}

	public Group(String name, Value<?>... values) {
		this(name, Arrays.asList(values));
	}
	
	public Group(String name, Collection<Value<?>> values) {
		this(name, values, new ColumnLayoutStyle());
	}
	
	public Group(String name, Collection<Value<?>> values, Style<List<Value<?>>> style) {
		this.name = name;
		this.values = new ArrayList<>(values);
		getValueHook().updateListeners(this.values);
		this.style = style;
		
		//Add listeners so that we can re-broadcast it as an event for this group
		for (Value<?> value : values) {
			value.getValueHook().addListener(o -> getValueHook().updateListeners(this.values));
		}
		
	}
	
	public String toString()
	{
		String str =  "Group: " + getName();
		return str;
	}

	@Override
	public Style<List<Value<?>>> getStyle() {
		return style;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean setValue(List<Value<?>> value) {
		values = value;
		return true;
	}

	@Override
	public List<Value<?>> getValue() {
		return values;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		getEnabledHook().updateListeners(isEnabled());
	}
	
	public EventfulType<List<Value<?>>> getValueHook() {
		return valueHook;
	}

	public EventfulType<Boolean> getEnabledHook() {
		return enabledHook;
	}
	
	public void visit(Consumer<Value<?>> visitor) {
		for (Value<?> value : getValue()) {
			visitor.accept(value);
			if (value instanceof Group) {
				((Group)value).visit(visitor);
			}
		}
	}
	
	
	public void deserialize(List<Object> stored) {
		Iterator<Object> iter = stored.iterator();
		visit(p -> {
			if (!iter.hasNext()) {
				throw new RuntimeException("Unexpected end of data");
			}
			Object item = iter.next();
			if (item instanceof String && p instanceof Parameter<?>) {
				((Parameter<?>)p).deserialize((String)item);
			} else if (item instanceof List<?> && p instanceof Group) {
				((Group)p).deserialize((List<Object>) item);
			} else {
				throw new RuntimeException("Structure mismatch");
			}
			
		});
	}
	
	public List<Object> serialize() {
		List<Object> dumped = new ArrayList<>();
		visit(p -> {
			if (p instanceof Parameter<?>) {
				dumped.add(((Parameter<?>) p).serialize());
			} else if (p instanceof Group) {
				dumped.add(((Group) p).serialize());
			}
		});
		return dumped;
	}
	
}
