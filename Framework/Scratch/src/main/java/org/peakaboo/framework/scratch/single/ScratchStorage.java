package org.peakaboo.framework.scratch.single;

import org.peakaboo.framework.scratch.ScratchEncoder;

public interface ScratchStorage<T> {
	T get();
	void put(T value);
	ScratchEncoder<T> getEncoder();
}
