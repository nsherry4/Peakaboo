package net.sciencestudio.scratch.list.file;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;
import net.sciencestudio.scratch.list.array.ScratchArrayList;

public class ScratchDiskList<T> extends AbstractList<T>{

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

