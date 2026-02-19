package org.peakaboo.framework.scratch.list;

import java.io.IOException;
import java.util.logging.Level;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.scratch.DiskStrategy;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.list.array.ScratchArrayList;
import org.peakaboo.framework.scratch.list.file.ScratchDiskList;

public class ScratchLists {

	public static <T> ScratchList<T> memoryBacked(ScratchEncoder<T> encoder) {
		return new ScratchArrayList<T>(encoder);
	}
	
	/**
	 * Creates a disk-backed ScratchList. The returned list implements AutoCloseable and
	 * should be explicitly closed when no longer needed to ensure proper cleanup of
	 * temporary files and file handles.
	 * 
	 * @param <T> the type of elements in the list
	 * @param encoder the encoder to use for serializing/deserializing elements
	 * @return a new disk-backed ScratchList
	 * @throws IOException if temporary file creation fails
	 */
	public static <T> ScratchList<T> diskBacked(ScratchEncoder<T> encoder) throws IOException {
		return new ScratchDiskList<>(encoder);
	}
	
	public static <T> ScratchList<T> tryDiskBacked(ScratchEncoder<T> encoder) {
		try {
			return diskBacked(encoder);
		} catch (IOException e) {
			OneLog.log(Level.SEVERE, "Could not allocate disk-backed store, using in-memory store instead", e);
			return memoryBacked(encoder);
		}
	}
	
	public static <T> ScratchList<T> get(DiskStrategy strategy, ScratchEncoder<T> encoder) {
		switch(strategy) {
		case PREFER_DISK:
			return tryDiskBacked(encoder);
		case REQUIRE_DISK:
			try {
				return diskBacked(encoder);
			} catch (IOException e) {
				throw new ScratchException(e);
			}
		case PREFER_MEMORY:
			return memoryBacked(encoder);
		default:
			throw new ScratchException(new RuntimeException("Unrecognized Disk Strategy"));
		}
	}
	
}
