package org.peakaboo.framework.cyclops;

public class Mutable<T> {

	private T value;

	public Mutable() {
		this.value = null;
	}
	
	public Mutable(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
	
}
