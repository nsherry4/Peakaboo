package scratch.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scratch.ScratchEncoder;
import scratch.ScratchException;

final class ScratchArrayList<T> implements ScratchList<T> {

	protected List<byte[]> data;
	protected ScratchEncoder<T> encoder;
	
	
	
	public ScratchEncoder<T> getEncoder() {
		return encoder;
	}
	public void setEncoder(ScratchEncoder<T> encoder) {
		this.encoder = encoder;
	}
	
	protected byte[] encode(T data) throws ScratchException {
		return encoder.encode(data);
	}
	protected T decode(byte[] data) throws ScratchException {
		return encoder.decode(data);
	}
	
	public ScratchArrayList() {
		data = new ArrayList<>();
	}
	
	//use your own backing -- also sublist constructor
	public ScratchArrayList(List<byte[]> data) {
		this.data = data;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			Iterator<byte[]> delegate = data.iterator();
			
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public T next() {
				return decode(delegate.next());
			}
		};
	}


	@Override
	public void add(T e) {
		data.add(encode(e));
	}

	@Override
	public boolean remove(T e) {
		return data.remove(encode(e));
	}



	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ScratchArrayList) {
			return data.equals(((ScratchArrayList) o).data);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public T get(int index) {
		return decode(data.get(index));
	}

	@Override
	public void set(int index, T element) {
		data.set(index, encode(element));
	}

	@Override
	public void add(int index, T element) {
		data.add(index, encode(element));
	}

	@Override
	public void remove(int index) {
		data.remove(index);
	}

	@Override
	public boolean contains(T e) {
		return data.contains(encode(e));
	}
	
}
