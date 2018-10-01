package net.sciencestudio.scratch.list.array;

import java.util.ArrayList;
import java.util.List;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.list.ScratchList;
import net.sciencestudio.scratch.single.Compressed;

public class ScratchArrayList<T> extends ScratchList<T>{

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
	
	public void setCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new RuntimeException("Cannot add Compressed element with different ScratchEncoder");
		}
		backing.set(index, compressed.getBytes());
	}
	

	@Override
	public void add(int index, T element) {
		backing.add(index, encoder.encode(element));
	}
	
	public void addCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new RuntimeException("Cannot add Compressed element with different ScratchEncoder");
		}
		backing.add(index, compressed.getBytes());
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

