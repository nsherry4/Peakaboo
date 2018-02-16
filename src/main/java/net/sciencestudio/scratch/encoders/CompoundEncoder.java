package net.sciencestudio.scratch.encoders;

import java.util.Arrays;
import java.util.List;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;

public class CompoundEncoder<T> implements ScratchEncoder<T> {

	ScratchEncoder<T> first;
	List<ScratchEncoder<byte[]>> encoders;
	
	
	public CompoundEncoder(ScratchEncoder<T> first, List<ScratchEncoder<byte[]>> encoders) {
		this.first = first;
		this.encoders = encoders;
	}
	
	public CompoundEncoder(ScratchEncoder<T> first, ScratchEncoder<byte[]>... encoders) {
		this.first = first;
		this.encoders = Arrays.asList(encoders);
	}

	@Override
	public byte[] encode(T data) throws ScratchException {
		byte[] work = first.encode(data);
		for (int i = 0; i < encoders.size(); i++) {
			work = encoders.get(i).encode(work);
		}
		return work;
	}

	@Override
	public T decode(byte[] data) throws ScratchException {
		for (int i = 0; i < encoders.size(); i++) {
			data = encoders.get(i).decode(data);
		}
		return first.decode(data);
	}
	
	
	
}
