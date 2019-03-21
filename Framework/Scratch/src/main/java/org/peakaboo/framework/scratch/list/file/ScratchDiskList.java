package org.peakaboo.framework.scratch.list.file;

import java.io.IOException;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.list.ScratchList;
import org.peakaboo.framework.scratch.single.Compressed;

public class ScratchDiskList<T> extends ScratchList<T>{

	private ScratchDiskBacking backing;
	private ScratchEncoder<T> encoder;
	
	
	public ScratchDiskList(ScratchEncoder<T> encoder) {
		try {
			this.encoder = encoder;
			this.backing = new ScratchDiskBacking();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}
	
	///////////////////////////////////////////////////
	// Core AbstractList Overrides
	///////////////////////////////////////////////////
	
	@Override
	public T get(int index) {
		byte[] bytes = backing.get(index);
		if (bytes == null) { return null; }
		return encoder.decode(bytes);
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

