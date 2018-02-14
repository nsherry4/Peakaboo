package net.sciencestudio.autodialog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import eventful.EventfulType;
import net.sciencestudio.autodialog.model.style.Style;
import net.sciencestudio.autodialog.model.style.layouts.ColumnLayoutStyle;

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
	
	public List<Object> dumpValues() {
		List<Object> flat = new ArrayList<>();
		visit(p -> {
			//Don't save Group values, they're just a list of the parameters we'll get to next
			if (p instanceof Parameter<?>) {
				flat.add(p.getValue());				
			}
		});
		return flat;
	}
	
	public void loadValues(List<Object> values) {
		final List<Object> copy = new ArrayList<>(values);
		visit(p -> {
			//Don't load Group values, they're just a list of the parameters we'll get to next
			if (p instanceof Parameter<?>) {
				((Value<Object>)p).setValue(copy.remove(0));			
			}
		});
	}
	
}
