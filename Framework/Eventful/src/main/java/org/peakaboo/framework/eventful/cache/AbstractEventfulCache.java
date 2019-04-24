package org.peakaboo.framework.eventful.cache;

import java.util.function.Supplier;

import org.peakaboo.framework.eventful.Eventful;

public class AbstractEventfulCache<T> extends Eventful implements EventfulCache<T> {

	private Supplier<T> supplier;
	
	public AbstractEventfulCache(Supplier<T> supplier) {
		this.supplier = supplier;
		/*
		 * events usually get pushed out on the UI's event thread/queue, but this can
		 * cause problems if the data invalidation events haven't propagated to the
		 * downstream caches when some other event accesses them. Instead, we run the
		 * invalidation events eagerly
		 */ 
		setUIThreadRunnerOverride(runnable -> runnable.run());
	}
	
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected T regenerate() {
		return supplier.get();
	}

}
