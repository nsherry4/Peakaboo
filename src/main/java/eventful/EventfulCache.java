package eventful;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * EventfulCache stores a value along with a way to recalculate that value when
 * invalidated. It also emits events notifying of invalidation and can listen
 * for events from other EventfulCache objects. This allows easy chain
 * invalidation and dependency handling; when data involved in the calculation
 * of this cached value changes, this value will automatically be invalidated.
 */
public class EventfulCache<T> extends EventfulEnum<EventfulCache.CacheEvents> {

	public static enum CacheEvents {
		INVALIDATED;
	}
	
	// When empty, indicates an invalidaed value
	private T value = null;
	private boolean hasValue = false;
	private Supplier<T> supplier;
	private EventfulEnumListener<EventfulCache.CacheEvents> listener;
	
	
	public EventfulCache(Supplier<T> supplier) {
		this.supplier = supplier;

		listener = event -> {
			if (event == CacheEvents.INVALIDATED) {
				invalidate();
			}
		};
	}
	
	public synchronized void invalidate() {
		value = null;
		hasValue = false;
		updateListeners(CacheEvents.INVALIDATED);
	}
	
	public synchronized T getValue() {
		if (!hasValue) {
			value = supplier.get();
			hasValue = true;
		}
		return value;
	}
	
	/**
	 * Mark this cached value as dependant (eg derived from) the given cached value.
	 * When the given cached value is invalidated, this value will be invalidated as
	 * well.
	 */
	public void addDependency(EventfulCache<?> dependency) {
		dependency.addListener(listener);
	}
	
	
	
	
}
