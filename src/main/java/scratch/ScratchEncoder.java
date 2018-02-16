package scratch;

import scratch.encoders.CompoundEncoder;

public interface ScratchEncoder<T> {

	byte[] encode(T data) throws ScratchException;
	T decode(byte[] data) throws ScratchException;
	
	default ScratchEncoder<T> then(ScratchEncoder<byte[]> next) {
		return new CompoundEncoder<>(this, next);
	}
}
