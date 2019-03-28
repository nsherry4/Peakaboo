package org.peakaboo.framework.scratch.map.file;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.list.ScratchList;
import org.peakaboo.framework.scratch.list.ScratchLists;
import org.peakaboo.framework.scratch.map.ScratchMap;

public class ScratchDiskMap<K, V> extends ScratchMap<K, V>{

	ScratchList<V> backing;
	Set<Map.Entry<K, V>> entries = new HashSet<Map.Entry<K, V>>() {
		
		/*
		 * The entryset iterator's remove method is required for mutable maps, but by
		 * default, it would not remove the entry from the backing list. We return a
		 * delegating iterator which will do this properly
		 */
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			Iterator<Map.Entry<K, V>> entryiter = entries.iterator();
			return new Iterator<Map.Entry<K, V>>() {

				ScratchDiskEntry<K, V> current = null;
				
				@Override
				public boolean hasNext() {
					return entryiter.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					current = (ScratchDiskEntry<K, V>) entryiter.next();
					return current;
				}
				
				@Override
				public void remove() {
			        entryiter.remove();
			        current.remove();
			    }
			};
		}
		
	};
	
	public ScratchDiskMap(ScratchEncoder<V> encoder) throws IOException {
		this.backing = ScratchLists.diskBacked(encoder);
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		return entries;
	}

	@Override
	public V put(K key, V value) {
		ScratchDiskEntry<K, V> entry = new ScratchDiskEntry<>(backing, key, value);
		entries.add(entry);
		return null;
	}
	
}

class ScratchDiskEntry<K, V> implements Map.Entry<K, V> {

	ScratchList<V> backing;
	int index;
	K key;
	
	public ScratchDiskEntry(ScratchList<V> backing, K key, V value) {
		this.backing = backing;
		this.key = key;
		synchronized(backing) {
			index = backing.size();
			backing.add(index, value);
		}
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return backing.get(index);
	}

	@Override
	public V setValue(V value) {
		V old = getValue();
		backing.set(index, value);
		return old;
	}
	
	public void remove() {
		backing.remove(index);
	}
	
}