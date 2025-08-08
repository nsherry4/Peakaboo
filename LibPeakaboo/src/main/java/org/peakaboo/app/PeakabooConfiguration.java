package org.peakaboo.app;

import org.peakaboo.dataset.encoder.QOXRF;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;

public class PeakabooConfiguration {

	public static ScratchEncoder<Spectrum> spectrumEncoder = new QOXRF();
	
	public static final MemorySize memorySize = calcMemoryFootprint();

	public enum MemorySize {
		TINY, SMALL, MEDIUM, LARGE;
	}

	private static MemorySize calcMemoryFootprint() {
		
		long maxHeap = Env.maxHeap();
		
		
		if      (maxHeap < 512)   return MemorySize.TINY;
		else if (maxHeap <= 2048) return MemorySize.SMALL;
		else if (maxHeap <= 8192) return MemorySize.MEDIUM;
		else                      return MemorySize.LARGE;
		
		
	}
}
