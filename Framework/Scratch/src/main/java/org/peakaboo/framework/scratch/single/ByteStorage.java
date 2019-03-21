package org.peakaboo.framework.scratch.single;

import org.peakaboo.framework.scratch.ScratchEncoder;

public interface ByteStorage<T> {

	T get();
	byte[] getBytes();
	ScratchEncoder<T> getEncoder();
	
}
