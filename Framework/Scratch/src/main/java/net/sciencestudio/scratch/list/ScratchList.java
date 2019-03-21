package net.sciencestudio.scratch.list;

import java.util.AbstractList;

import net.sciencestudio.scratch.single.Compressed;

public abstract class ScratchList<T> extends AbstractList<T> {
	
	public void addCompressed(Compressed<T> compressed) {
		addCompressed(size(), compressed);
	}
		
	public abstract void addCompressed(int index, Compressed<T> compressed);
	public abstract void setCompressed(int index, Compressed<T> compressed);
	
}
