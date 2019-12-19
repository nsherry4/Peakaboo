package org.peakaboo.framework.scratch.memoized;

import java.util.function.Predicate;

import org.peakaboo.framework.scratch.DiskStrategy;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class MemoizedPredicate<K> extends MemoizedCallable<K, Boolean> implements Predicate<K> {

	private Predicate<K> predicate;

	public MemoizedPredicate() {
		this(DiskStrategy.PREFER_MEMORY, null);
	}
	
	public MemoizedPredicate(DiskStrategy disk) {
		this(disk, null);
	}
	
	public MemoizedPredicate(Predicate<K> predicate) {
		this(DiskStrategy.PREFER_MEMORY, predicate);
	}
	
	public MemoizedPredicate(DiskStrategy disk, Predicate<K> predicate) {
		super(disk, Serializers.fst(Boolean.class));
		this.predicate = predicate;
	}
	
	public void setPredicate(Predicate<K> predicate) {
		this.predicate = predicate;
		clear();
	}

	@Override
	public boolean test(K k) {
		if (predicate == null) {
			throw new ScratchException(new RuntimeException("Function must be initialized"));
		}
		Boolean value;
		if (!has(k)) {
			value = predicate.test(k);
			put(k, value);
		} else {
			value = get(k);
		}
		return value;
	}
	
	public static void main(String[] args) {
		MemoizedPredicate<Integer> isEven = new MemoizedPredicate<>();
		isEven.setPredicate(i -> {
			//if it's stupid but it works, it's not stupid
			//besides, we need something slow to test memoization 
			if (i == 0) { return true; }
			if (i == 1) { return false; }
			return !isEven.test(i-1) & isEven.test(i-2);
		});
		
		long t1 = System.currentTimeMillis();
		System.out.println(isEven.test(34));
		long t2 = System.currentTimeMillis();
		System.out.println("Time: " + (t2-t1) + "ms");
	}

}
