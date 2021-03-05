package org.peakaboo.controller.mapper.fitting.modes.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;

public class TranslationState extends AbstractState {

	private Map<Integer, List<Integer>> translation = new LinkedHashMap<>();
	private boolean valid;
	
	public TranslationState(ModeController mode) {
		super(mode);
	}

	public void initialize(int size) {
		translation.clear();
		for (int i = 0; i < size; i++) {
			translation.put(i, new ArrayList<>());
		}
		this.valid = true;
	}
	
	public void add(int bin, int index) {
		translation.get(bin).add(index);
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void invalidate() {
		this.valid = false;
	}
	
	public List<Integer> toSpatial(List<Integer> points) {
		if (isValid()) {
			mode.getData();
		}
		Set<Integer> translated = new HashSet<>();
		for (int i : points) {
			translated.addAll(translation.get(i));
		}
		return new ArrayList<>(translated);
	}	
	
		
}
