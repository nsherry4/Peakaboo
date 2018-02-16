package scratch.list;

import java.io.IOException;
import java.util.List;

import scratch.ScratchEncoder;

public class ScratchLists {

	public static <T> List<T> memoryBacked(ScratchEncoder<T> encoder) {
		ScratchList<T> slist = new ScratchArrayList<T>();
		slist.setEncoder(encoder);
		return new ScratchListAdapter<T>(slist);
	}
	
	public static <T> List<T> diskBacked(ScratchEncoder<T> encoder) throws IOException {
		ScratchList<T> slist = new ScratchDiskList<T>();
		slist.setEncoder(encoder);
		return new ScratchListAdapter<T>(slist);
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
