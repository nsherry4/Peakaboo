package scratch.single;

import scratch.ScratchEncoder;

public interface ByteStorage<T> {

	T get();
	byte[] getBytes();
	ScratchEncoder<T> getEncoder();
	
}
