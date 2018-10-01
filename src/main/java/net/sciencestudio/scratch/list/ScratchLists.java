package net.sciencestudio.scratch.list;

import java.io.IOException;
import java.util.logging.Level;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchLog;
import net.sciencestudio.scratch.list.array.ScratchArrayList;
import net.sciencestudio.scratch.list.file.ScratchDiskList;

public class ScratchLists {

	public static <T> ScratchList<T> memoryBacked(ScratchEncoder<T> encoder) {
		return new ScratchArrayList<T>(encoder);
	}
	
	public static <T> ScratchList<T> diskBacked(ScratchEncoder<T> encoder) throws IOException {
		return new ScratchDiskList<>(encoder);
	}
	
	public static <T> ScratchList<T> tryDiskBacked(ScratchEncoder<T> encoder) {
		try {
			return diskBacked(encoder);
		} catch (IOException e) {
			ScratchLog.get().log(Level.SEVERE, "Could not allocate disk-backed store, using in-memory store instead", e);
			return memoryBacked(encoder);
		}
	}
	
}
