package org.peakaboo.framework.scratch.list;

import java.util.AbstractList;

import org.peakaboo.framework.scratch.single.Compressed;

public abstract class ScratchList<T> extends AbstractList<T> {
	
	public void addCompressed(Compressed<T> compressed) {
		addCompressed(size(), compressed);
	}
		
	public abstract void addCompressed(int index, Compressed<T> compressed);
	public abstract void setCompressed(int index, Compressed<T> compressed);
	
}
