package org.peakaboo.framework.accent;

import java.util.function.Predicate;

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
	
	public synchronized T getSynchronized() {
		return get();
	}
	
	public synchronized void setSynchronized(T value) {
		set(value);
	}
	
	public synchronized boolean testAndSetSynchronized(Predicate<T> test, T value) {
		boolean testResult = test.test(getSynchronized());
		if (testResult) {
			set(value);
		}
		return testResult;
	}
	
}
