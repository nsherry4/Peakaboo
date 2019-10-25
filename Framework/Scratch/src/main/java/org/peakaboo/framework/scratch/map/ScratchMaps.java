package org.peakaboo.framework.scratch.map;

import java.io.IOException;
import java.util.logging.Level;

import org.peakaboo.framework.scratch.DiskStrategy;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.peakaboo.framework.scratch.ScratchLog;
import org.peakaboo.framework.scratch.list.ScratchList;
import org.peakaboo.framework.scratch.map.file.ScratchDiskMap;
import org.peakaboo.framework.scratch.map.memory.ScratchHashMap;

public class ScratchMaps {

	public static <K, V> ScratchMap<K, V> memoryBacked(ScratchEncoder<V> encoder) {
		return new ScratchHashMap<K, V>(encoder);
	}
	
	public static <K, V> ScratchMap<K, V> diskBacked(ScratchEncoder<V> encoder) throws IOException {
		return new ScratchDiskMap<>(encoder);
	}
	
	public static <K, V> ScratchMap<K, V> tryDiskBacked(ScratchEncoder<V> encoder) {
		try {
			return diskBacked(encoder);
		} catch (IOException e) {
			ScratchLog.get().log(Level.SEVERE, "Could not allocate disk-backed store, using in-memory store instead", e);
			return memoryBacked(encoder);
		}
	}
	
	public static <K, V> ScratchMap<K, V> get(DiskStrategy strategy, ScratchEncoder<V> encoder) {
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
