package net.sciencestudio.scratch.list;

import java.io.IOException;
import java.util.List;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.list.array.ScratchArrayList;
import net.sciencestudio.scratch.list.file.ScratchDiskList;

public class ScratchLists {

	public static <T> List<T> memoryBacked(ScratchEncoder<T> encoder) {
		return new ScratchArrayList<T>(encoder);
	}
	
	public static <T> List<T> diskBacked(ScratchEncoder<T> encoder) throws IOException {
		return new ScratchDiskList<>(encoder);
	}
	
	public static <T> List<T> tryDiskBacked(ScratchEncoder<T> encoder) {
		try {
			return diskBacked(encoder);
		} catch (IOException e) {
			e.printStackTrace();
			return memoryBacked(encoder);
		}
	}
	
}
