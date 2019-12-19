package org.peakaboo.framework.scratch.memoized;

import org.peakaboo.framework.scratch.DiskStrategy;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.map.ScratchMap;
import org.peakaboo.framework.scratch.map.ScratchMaps;

public abstract class MemoizedCallable<K, V> {

	private ScratchMap<K, V> store;

	public MemoizedCallable(DiskStrategy disk, ScratchEncoder<V> encoder) {
		store = ScratchMaps.<K, V>get(disk, encoder);
	}
	
	protected boolean has(K key) {
		return store.containsKey(key);
	}
	
	protected void put(K k, V v) {
		store.put(k, v);
	}
	
	protected V get(K k) {
		return store.get(k);
	}
	
	protected void clear() {
		store.clear();
	}
}
