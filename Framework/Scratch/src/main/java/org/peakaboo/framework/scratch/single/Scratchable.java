package org.peakaboo.framework.scratch.single;

import java.lang.ref.SoftReference;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

/**
 * Represents an object which <i>can</i> be serialized to disk. The JVM has a
 * maximum heap size that the developer cannot always control. Scratchable
 * objects are serialized and written to disk, but also stored in a
 * {@link SoftReference} to minimize overhead. <br/>
 * <br/>
 * Care should be taken not to change the contents or state of the stored value,
 * as it may be reclaimed by the JVM at any time. If that happens, the next
 * retrieval of the value will be from disk, and those changes will be lost.
 */
public class Scratchable<T> implements ScratchStorage<T> {

	SoftReference<T> unscratched;
	Scratched<T> scratched;
	
	private Scratchable(T value, ScratchEncoder<T> encoder) throws ScratchException {
		this.scratched = Scratched.create(value, encoder);
		unscratched = new SoftReference<>(value);
	}
	
	public static <T> Scratchable<T> create(T value, ScratchEncoder<T> encoder) throws ScratchException {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		Scratchable<T> c = new Scratchable<>(value, encoder);
		return c;
	}
	

	
	@Override
	public T get() {
		T value = unscratched.get();
		if (value == null) {
			value = scratched.get();
			unscratched = new SoftReference<>(value);
		}
		return value;
	}
	
	@Override
	public void put(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		this.scratched.put(value);
		unscratched = new SoftReference<>(value);
	}

	@Override
	public ScratchEncoder<T> getEncoder() {
		return scratched.getEncoder();
	}

}
