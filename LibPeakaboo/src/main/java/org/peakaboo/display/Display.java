package org.peakaboo.display;

import org.peakaboo.common.PeakabooConfiguration;
import org.peakaboo.common.PeakabooConfiguration.MemorySize;
import org.peakaboo.framework.cyclops.Coord;

public class Display {

	public static final float OVERSIZE = 1.2f;
	
	private Display() {
		//Not Constructable
	}
	
	public static boolean useBuffer(Coord<Integer> size) {
		
		boolean doBuffer = true;
		
		int bufferSpace = (int)((size.x * OVERSIZE * size.y * OVERSIZE * 4) / 1024f / 1024f);
		if (bufferSpace > 10 && PeakabooConfiguration.memorySize == MemorySize.TINY) {
			doBuffer = false;
		}
		if (bufferSpace > 20 && PeakabooConfiguration.memorySize == MemorySize.SMALL) {
			doBuffer = false;
		}
		if (bufferSpace > 40 && PeakabooConfiguration.memorySize == MemorySize.MEDIUM) {
			doBuffer = false;
		}
		if (bufferSpace > 250 && PeakabooConfiguration.memorySize == MemorySize.LARGE) {
			doBuffer = false;
		}
		
		Runtime rt = Runtime.getRuntime();
		int freemem = (int) (rt.freeMemory() / 1024f / 1024f);
		if (bufferSpace * 1.5f > freemem) {
			doBuffer = false;
		}
		
		return doBuffer;
		
	}
	
}
