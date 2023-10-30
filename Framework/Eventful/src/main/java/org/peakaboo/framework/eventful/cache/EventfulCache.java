package org.peakaboo.framework.eventful.cache;

import org.peakaboo.framework.eventful.IEventfulBeacon;

/**
 * EventfulCache stores a value along with a way to recalculate that value when
 * invalidated. It also emits events notifying of invalidation and can listen
 * for events from other EventfulCache objects. This allows easy chain
 * invalidation and dependency handling; when data involved in the calculation
 * of this cached value changes, this value will automatically be invalidated.
 */
public interface EventfulCache<T> extends IEventfulBeacon {

	void invalidate();

	T getValue();

	/**
	 * Mark this cached value as dependent on (ie derived from) the given cached value.
	 * When the upstream cached value is invalidated, this value will be invalidated as
	 * well. Note that unlike regular listeners, the dependency invalidation is done
	 * immediately, rather than on the ui event thread.
	 */
	default void addUpstreamDependency(EventfulCache<?> dependency) {
		dependency.addDependent(this);
	}

	default void addDependent(EventfulCache<?> downstream) {
		addListener(downstream::invalidate);
	}

	
}