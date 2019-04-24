package org.peakaboo.framework.eventful.cache;

import java.util.function.Supplier;

/**
 * EventfulNullableCache is a kind of EventfulCache which can store null values
 */
public class EventfulNullableCache<T> extends AbstractEventfulCache<T> {
	
	// When empty, indicates an invalidaed value
	private T value = null;
	private boolean hasValue = false;
		
	public EventfulNullableCache(Supplier<T> supplier) {
		super(supplier);
	}
	
	@Override
	public void invalidate() {
		// We don't want to hold the lock for updating the deps to prevent deadlock
		// where getValue locks from the bottom up and invalidate locks from the top
		// down.
		synchronized (this) {
			value = null;
			hasValue = false;
		}
		updateListeners();
	}
	
	@Override
	public T getValue() {
		if (!hasValue) {
			synchronized (this) {
				if (!hasValue) {
					value = regenerate();
					hasValue = true;
				}
			}
		}
		return value;
	}
	
}
