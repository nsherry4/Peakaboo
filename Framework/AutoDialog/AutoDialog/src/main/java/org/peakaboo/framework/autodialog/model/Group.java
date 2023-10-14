package org.peakaboo.framework.autodialog.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.peakaboo.framework.autodialog.model.style.Style;
import org.peakaboo.framework.autodialog.model.style.layouts.ColumnLayoutStyle;

public class Group extends SimpleValue<List<Value<?>>> {

	public Group(String name) {
		this(name, new ArrayList<>(), new ColumnLayoutStyle());
	}

	public Group(String name, Style<List<Value<?>>> style) {
		this(name, new ArrayList<>(), style);
	}

	public Group(String name, Style<List<Value<?>>> style, Value<?>... values) {
		this(name, Arrays.asList(values), style);
	}

	public Group(String name, Value<?>... values) {
		this(name, Arrays.asList(values));
	}
	
	public Group(String name, List<Value<?>> values) {
		this(name, values, new ColumnLayoutStyle());
	}
	
	public Group(String name, List<Value<?>> value, Style<List<Value<?>>> style) {
		// Constructor with a copy of the list to prevent surprises 
		// from later external modification of the original
		super(name, style, new ArrayList<>(value));
		
		//Add listeners so that we can re-broadcast it as an event for this group
		for (var v : this.value) {
			v.getValueHook().addListener(o -> getValueHook().updateListeners(this.value));
		}
		
	}
	
	public String toString() {
		return "Group: " + getName();
	}




	@Override
	public boolean setValue(List<Value<?>> value) {
		// Don't emit an event for setting the list of items, only for changing their values 
		this.value = value;
		return true;
	}

	
	public void visit(Consumer<Value<?>> visitor) {
		for (Value<?> value : getValue()) {
			visitor.accept(value);
			if (value instanceof Group g) {
				g.visit(visitor);
			}
		}
	}
	
	
	public void deserializeMap(Map<String, Object> stored) {
		visit(param -> {
			var name = param.getName();
			if ("null".equals(name)) return;
			if (stored.containsKey(name)) {
				var storedValue = stored.get(name);
				if (storedValue instanceof String s && param instanceof Parameter<?> p) {
					p.deserialize(s);
				} else if (storedValue instanceof List<?> && param instanceof Group g) {
					g.deserialize((List<Object>) storedValue);
				} else {
					throw new RuntimeException("Structure mismatch");
				}
			}
		});
	}
	
	@Deprecated(since = "6", forRemoval = true)
	public void deserialize(List<Object> stored) {
		Iterator<Object> iter = stored.iterator();
		visit(param -> {
			if (!iter.hasNext()) {
				throw new RuntimeException("Unexpected end of data");
			}
			Object item = iter.next();
			if (item instanceof String s && param instanceof Parameter<?> p) {
				p.deserialize(s);
			} else if (item instanceof List<?> && param instanceof Group g) {
				g.deserialize((List<Object>) item);
			} else {
				throw new RuntimeException("Structure mismatch");
			}
			
		});
	}
	
	@Deprecated(since = "6", forRemoval = true)
	public List<Object> serialize() {
		List<Object> dumped = new ArrayList<>();
		visit(param -> {
			if (param instanceof Parameter<?> p) {
				dumped.add(p.serialize());
			} else if (param instanceof Group g) {
				dumped.add(g.serialize());
			}
		});
		return dumped;
	}
	
	public Map<String, Object> serializeMap() {
		Map<String, Object> dumped = new HashMap<>();
		visit(param -> {
			if (param instanceof Parameter<?> p) {
				dumped.put(p.getName(), p.serialize());
			} else if (param instanceof Group) {
				Group group = (Group) param;
				dumped.put(group.getName(), group.serialize());
			}
		});
		return dumped;
	}
	
}
