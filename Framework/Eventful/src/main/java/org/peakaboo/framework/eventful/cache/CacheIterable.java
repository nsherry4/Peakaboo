package org.peakaboo.framework.eventful.cache;

import java.util.Iterator;

public class CacheIterable<T> implements Iterable<T> {

	private Iterable<? extends EventfulCache<T>> caches;
	
	public CacheIterable(Iterable<? extends EventfulCache<T>> caches) {
		this.caches = caches;
	}
	
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<? extends EventfulCache<T>> backer = caches.iterator();
			@Override
			public boolean hasNext() {
				return backer.hasNext();
			}

			@Override
			public T next() {
				return backer.next().getValue();
			}
		};
	}
	
}
