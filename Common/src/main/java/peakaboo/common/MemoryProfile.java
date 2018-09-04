package peakaboo.common;

public class MemoryProfile {

	public static final Size size = calcMemoryFootprint();
	
	public enum Size {
		SMALL, MEDIUM, LARGE;
	}

	private static Size calcMemoryFootprint() {
		
		long maxHeap = Env.maxHeap();
		
		if (maxHeap < 128) {
			return Size.SMALL;
		} else if (maxHeap < 1024) {
			return Size.MEDIUM;
		} else {
			return Size.LARGE;
		}
		
	}
	
}
