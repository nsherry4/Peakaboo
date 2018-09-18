package peakaboo.common;


public class PeakabooConfiguration {

	public static boolean compression = true;
	public static boolean diskstore = true;
	public static MemorySize memorySize = calcMemoryFootprint();

	public enum MemorySize {
		SMALL, MEDIUM, LARGE;
	}

	private static MemorySize calcMemoryFootprint() {
		
		long maxHeap = Env.maxHeap();
		
		if (maxHeap < 128) {
			return MemorySize.SMALL;
		} else if (maxHeap < 1024) {
			return MemorySize.MEDIUM;
		} else {
			return MemorySize.LARGE;
		}
		
	}
	
}
