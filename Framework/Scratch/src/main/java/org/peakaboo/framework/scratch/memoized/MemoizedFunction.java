package org.peakaboo.framework.scratch.memoized;

import java.util.function.Function;

import org.peakaboo.framework.scratch.DiskStrategy;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class MemoizedFunction<K, V> extends MemoizedCallable<K, V> implements Function<K, V> {

	private Function<K, V> function;

	public MemoizedFunction(Class<V> cls) {
		this(DiskStrategy.PREFER_MEMORY, new CompoundEncoder<>(Serializers.fst(cls)));
	}
	
	public MemoizedFunction(DiskStrategy disk, ScratchEncoder<V> encoder) {
		super(disk, encoder);
	}
	
	public MemoizedFunction(DiskStrategy disk, ScratchEncoder<V> encoder, Function<K, V> function) {
		super(disk, encoder);
		this.function = function;
	}
	
	public void setFunction(Function<K, V> function) {
		this.function = function;
		clear();
	}

	@Override
	public V apply(K k) {
		if (function == null) {
			throw new ScratchException(new RuntimeException("Function must be initialized"));
		}
		V value;
		if (!has(k)) {
			value = function.apply(k);
			put(k, value);
		} else {
			value = get(k);
		}
		return value;
	}
	
	public static void main(String[] args) {
		MemoizedFunction<Integer, Integer> fib = new MemoizedFunction<>(Integer.class);
		fib.setFunction(i -> {
			if (i == 0) { return 0; }
			if (i == 1) { return 1; }
			return fib.apply(i-1) + fib.apply(i-2);
		});
		
		long t1 = System.currentTimeMillis();
		System.out.println(fib.apply(45));
		long t2 = System.currentTimeMillis();
		System.out.println("Time: " + (t2-t1) + "ms");
	}
	
}
