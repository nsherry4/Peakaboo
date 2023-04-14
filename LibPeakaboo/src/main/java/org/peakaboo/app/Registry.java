package org.peakaboo.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Registry<S, T> {

	private Map<String, Function<S, T>> registry = new LinkedHashMap<>();

	public T create(String type, S input) {
		if (registry.containsKey(type)) {
			return registry.get(type).apply(input);
		} else {
			throw new RuntimeException("Unknown type: " + type);
		}
	}
	
	public String defaultType() {
		return registry.entrySet().iterator().next().getKey();
	}
	
	public List<String> typeNames() {
		return new ArrayList<>(registry.keySet());
	}
	
	public void register(String type, Function<S, T> constructor) {
		if (registry.containsKey(type)) {
			throw new RuntimeException("Map mode " + type + " is already registered");
		}
		registry.put(type, constructor);
	}
	
	
}
