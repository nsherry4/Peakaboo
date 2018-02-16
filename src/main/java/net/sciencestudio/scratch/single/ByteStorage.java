package net.sciencestudio.scratch.single;

import net.sciencestudio.scratch.ScratchEncoder;

public interface ByteStorage<T> {

	T get();
	byte[] getBytes();
	ScratchEncoder<T> getEncoder();
	
}
