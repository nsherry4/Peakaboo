package net.sciencestudio.scratch.list;

import java.util.Collection;

import net.sciencestudio.scratch.ScratchEncoder;

interface ScratchList<T> extends Iterable<T>{

	ScratchEncoder<T> getEncoder();
	void setEncoder(ScratchEncoder<T> encoder);
	
	int size();
	void add(T e);
	void add(int index, T e);
	boolean remove(T e);
	void remove(int index);
	T get(int index);
	void set(int index, T value);
	void clear();
	boolean contains(T o);

	default boolean isEmpty() {
		return size() == 0;
	}
	default void addAll(Collection<? extends T> c) {
		for (T o : c) {
			add(o);
		}
	}
	default void addAll(int index, Collection<? extends T> c) {
		for (T o : c) {
			add(index, o);
		}
	}
	default boolean containsAll(Collection<? extends T> c) {
		for (T t : this) {
			if (!contains(t)) { return false; }
		}
		return true;
	}
	
	
}


