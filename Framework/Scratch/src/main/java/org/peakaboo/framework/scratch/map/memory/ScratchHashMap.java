package org.peakaboo.framework.scratch.map.memory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.map.ScratchMap;

public class ScratchHashMap<K, V> extends ScratchMap<K, V> {

	Set<Map.Entry<K, V>> entries = new HashSet<>();
	ScratchEncoder<V> encoder;
	
	public ScratchHashMap(ScratchEncoder<V> encoder) {
		this.encoder = encoder;
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		return entries;
	}
	
	@Override
	public V put(K key, V value) {
		if (this.containsKey(key)) {
			for (Map.Entry<K, V> entry : entries) {
				if (entry.getKey() == key) {
					return entry.setValue(value);
				}
			}
		} else {
			entries.add(new ScratchMemoryMapEntry<>(key, value, encoder));
			return null;
		}
		return null;
	}

}

class ScratchMemoryMapEntry<K, V> implements Map.Entry<K, V> {

	K key;
	byte[] value;
	ScratchEncoder<V> encoder;
	
	public ScratchMemoryMapEntry(K key, V value, ScratchEncoder<V> encoder) {
		this.encoder = encoder;
		this.key = key;
		this.value = encoder.encode(value);
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return encoder.decode(value);
	}

	@Override
	public V setValue(V value) {
		V old = getValue();
		this.value = encoder.encode(value);
		return old;
	}
	
}