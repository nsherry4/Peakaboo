package org.peakaboo.framework.scratch.list.file;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.list.ScratchList;
import org.peakaboo.framework.scratch.single.Compressed;

/**
 * A disk-backed implementation of ScratchList that stores data in temporary files.
 * This implementation implements AutoCloseable and should be explicitly closed when
 * no longer needed to ensure proper cleanup of temporary files and file handles.
 * 
 * <p>Unlike try-with-resources patterns, instances of this class are typically
 * long-lived and should be closed when the application determines they are no
 * longer needed, such as in response to user actions or application shutdown.</p>
 */
public class ScratchDiskList<T> extends ScratchList<T> implements AutoCloseable {

	private ScratchDiskBacking backing;
	private ScratchEncoder<T> encoder;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	
	public ScratchDiskList(ScratchEncoder<T> encoder) {
		try {
			this.encoder = encoder;
			this.backing = new ScratchDiskBacking();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}
	
	///////////////////////////////////////////////////
	// Core AbstractList Overrides
	///////////////////////////////////////////////////
	
	@Override
	public ScratchEncoder<T> getEncoder() {
		return encoder;
	}
	
	@Override
	public T get(int index) {
		byte[] bytes;
		
		// Get the data safely under read lock - allows concurrent reads
		lock.readLock().lock();
		try {
			bytes = backing.get(index);
		} finally {
			lock.readLock().unlock();
		}
		
		// Decode outside the lock (the expensive operation)
		if (bytes == null) { return null; }
		return encoder.decode(bytes);
	}
	
	/**
	 * Get element without acquiring any locks. Only safe to call when already holding a lock.
	 */
	private T getWithoutLock(int index) {
		byte[] bytes = backing.get(index);
		if (bytes == null) { return null; }
		return encoder.decode(bytes);
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return backing.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public T set(int index, T element) {
		lock.writeLock().lock();
		try {
			T oldValue = getWithoutLock(index);
			backing.set(index, encoder.encode(element));
			return oldValue;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void setCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new IllegalArgumentException("Cannot add Compressed element with different ScratchEncoder");
		}
		lock.writeLock().lock();
		try {
			backing.set(index, compressed.getBytes());
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void add(int index, T element) {
		lock.writeLock().lock();
		try {
			backing.add(index, encoder.encode(element));
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void addCompressed(int index, Compressed<T> compressed) {
		if (!compressed.getEncoder().equals(encoder)) {
			throw new IllegalArgumentException("Cannot add Compressed element with different ScratchEncoder");
		}
		lock.writeLock().lock();
		try {
			backing.add(index, compressed.getBytes());
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public T remove(int index) {
		lock.writeLock().lock();
		try {
			T oldValue = getWithoutLock(index);
			backing.remove(index);
			return oldValue;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void close() throws IOException {
		if (backing != null) {
			backing.close();
		}
	}

	///////////////////////////////////////////////////
	// Performance-Related Overrides
	///////////////////////////////////////////////////

	@Override
	public void clear() {
		lock.writeLock().lock();
		try {
			backing.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	
}

