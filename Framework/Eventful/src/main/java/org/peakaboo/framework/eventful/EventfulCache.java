package org.peakaboo.framework.eventful;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * EventfulCache stores a value along with a way to recalculate that value when
 * invalidated. It also emits events notifying of invalidation and can listen
 * for events from other EventfulCache objects. This allows easy chain
 * invalidation and dependency handling; when data involved in the calculation
 * of this cached value changes, this value will automatically be invalidated.
 */
public class EventfulCache<T> extends Eventful {
	
	// When empty, indicates an invalidaed value
	private T value = null;
	private boolean hasValue = false;
	private Supplier<T> supplier;
	
	private Eventful deps;
	
	
	public EventfulCache(Supplier<T> supplier) {
		this.supplier = supplier;
		this.deps = new Eventful();
		/*
		 * events usually get pushed out on the UI's event thread/queue, but this can
		 * cause problems if the data invalidation events haven't propagated to the
		 * downstream caches when some other event accesses them. Instead, we run the
		 * invalidation events eagerly
		 */ 
		deps.setUIThreadRunnerOverride(runnable -> runnable.run());
	}
	
	public void invalidate() {
		// We don't want to hold the lock for updating the deps to prevent deadlock
		// where getValue locks from the bottom up and invalidate locks from the top
		// down.
		synchronized (this) {
			value = null;
			hasValue = false;
		}
		deps.updateListeners();
		updateListeners();
	}
	
	public T getValue() {
		if (!hasValue) {
			synchronized (this) {
				if (!hasValue) {
					value = supplier.get();
					hasValue = true;
				}
			}
		}
		return value;
	}
	
	/**
	 * Mark this cached value as dependant (eg derived from) the given cached value.
	 * When the given cached value is invalidated, this value will be invalidated as
	 * well. Note that unlike regular listeners, the dependency invalidation is done
	 * immediately, rather than on the ui event thread.
	 */
	public void addUpstreamDependency(EventfulCache<?> dependency) {
		dependency.deps.addListener(this::invalidate);
	}
	
	
	
	
	
	
}
