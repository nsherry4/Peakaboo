package net.sciencestudio.scratch.list.array;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sciencestudio.scratch.ScratchEncoder;

public class ScratchArrayList<T> extends AbstractList<T>{

	private List<byte[]> backing;
	protected ScratchEncoder<T> encoder;
	
	public ScratchArrayList(ScratchEncoder<T> encoder) {
		this.backing = new ArrayList<>();
		this.encoder = encoder;
	}
	
	///////////////////////////////////////////////////
	// Core AbstractList Overrides
	///////////////////////////////////////////////////
	
	@Override
	public T get(int index) {
		return encoder.decode(backing.get(index));
	}
	
	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public T set(int index, T element) {
		T t = get(index);
		backing.set(index, encoder.encode(element));
		return t;
	}
	

	@Override
	public void add(int index, T element) {
		backing.add(index, encoder.encode(element));
	}
	
	@Override
	public T remove(int index) {
		T t = get(index);
		backing.remove(index);
		return t;
	}
	
	
	
	
	
	
	///////////////////////////////////////////////////
	// Performance-Related Overrides
	///////////////////////////////////////////////////

	@Override
	public void clear() {
		backing.clear();
	}
	
}

