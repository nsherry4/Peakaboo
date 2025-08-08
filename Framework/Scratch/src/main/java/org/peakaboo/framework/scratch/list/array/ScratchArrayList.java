package org.peakaboo.framework.scratch.list.array;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.list.ScratchList;
import org.peakaboo.framework.scratch.single.Compressed;

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
		byte[] encodedValue;
		
		// Get the data safely under synchronization (minimal lock time)
		synchronized (this) {
			if (index >= backing.size()) { 
				return null; 
			}
			encodedValue = backing.get(index);
		}
		
		// Decode outside the lock (the expensive operation)
		if (encodedValue == null) { 
			return null; 
		}
		return encoder.decode(encodedValue);
	}
	
	@Override
	public synchronized int size() {
		return backing.size();
	}
	
	@Override
	public ScratchEncoder<T> getEncoder() {
		return encoder;
	}

	@Override
	public synchronized T set(int index, T element) {
		T t = get(index);
		
		ensureCapacity(index);
		
		//add or set based on if we're extending this list by 1
		if (backing.size() == index) {
			backing.add(encoder.encode(element));	
		} else {
			backing.set(index, encoder.encode(element));
		}
		return t;
	}
	
	public synchronized void setCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new IllegalArgumentException("Cannot add Compressed element with different ScratchEncoder");
		}
		
		ensureCapacity(index);
		
		//add or set based on if we're extending this list by 1
		byte[] bytes = compressed.getBytes();
		if (backing.size() == index) {
			backing.add(bytes);	
		} else {
			backing.set(index, bytes);
		}
	}

	/**
	 * Extends the backing array with nulls to ensure that calls to the set method
	 * work as expected.
	 * 
	 * @param index The index for which a set call on the backer must succeed after
	 *              this method has completed.
	 */
	private synchronized void ensureCapacity(int index) {
		while (backing.size() <= index) {
			backing.add(null);
		}
	}
	

	@Override
	public synchronized void add(int index, T element) {
		backing.add(index, encoder.encode(element));
	}
	
	public synchronized void addCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new IllegalArgumentException("Cannot add Compressed element with different ScratchEncoder");
		}
		backing.add(index, compressed.getBytes());
	}
	
	@Override
	public synchronized T remove(int index) {
		T t = get(index);
		backing.remove(index);
		return t;
	}

	@Override
	public void close() throws IOException {
		// No-op
	}

	///////////////////////////////////////////////////
	// Performance-Related Overrides
	///////////////////////////////////////////////////

	@Override
	public synchronized void clear() {
		backing.clear();
	}



}

