package org.peakaboo.controller.mapper.fitting.modes.components;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class TranslationState extends AbstractState {

	private Map<Integer, IntArrayList> translation = new LinkedHashMap<>();
	private boolean valid;
	
	public TranslationState(ModeController mode) {
		super(mode);
	}

	public void initialize(int size) {
		translation.clear();
		for (int i = 0; i < size; i++) {
			translation.put(i, new IntArrayList());
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
	
	public IntArrayList toSpatial(IntArrayList points) {
		if (!isValid()) {
			//regenerate data, including translation map
			mode.getData();
		}
		Set<Integer> translated = new HashSet<>();
		for (int i : points) {
			translated.addAll(translation.get(i));
		}
		return new IntArrayList(translated);
	}	
	
		
}
