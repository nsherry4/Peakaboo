package org.peakaboo.framework.eventful.cache;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

/**
 * EventfulSoftCache is a kind of EventfulCache which stores if values in a {@link SoftReference}
 */
public class EventfulSoftCache<T> extends AbstractEventfulCache<T> {

	private SoftReference<T> ref = new SoftReference<>(null);
	
	public EventfulSoftCache(Supplier<T> supplier) {
		super(supplier);
	}
	
	@Override
	public void invalidate() {
		synchronized (this) {
			ref = new SoftReference<>(null);
		}
	}

	@Override
	public T getValue() {
		T value = ref.get();
		if (value == null) {
			synchronized (this) {
				value = ref.get();
				if (value == null) {
					value = regenerate();
					ref = new SoftReference<>(value);
				}
			}
		}
		return value;
	}

}
