package org.peakaboo.framework.eventful.cache;

import java.util.function.Supplier;

import org.peakaboo.framework.eventful.EventfulBeacon;

public abstract class AbstractEventfulCache<T> extends EventfulBeacon implements EventfulCache<T> {

	private Supplier<T> supplier;
	
	public AbstractEventfulCache(Supplier<T> supplier) {
		this.supplier = supplier;
		/*
		 * events usually get pushed out on the UI's event thread/queue, but this can
		 * cause problems if the data invalidation events haven't propagated to the
		 * downstream caches when some other event accesses them. Instead, we run the
		 * invalidation events eagerly
		 */ 
		setUIThreadRunnerOverride(Runnable::run);
	}
		
	protected T regenerate() {
		return supplier.get();
	}

}
