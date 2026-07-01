package org.peakaboo.app;

import org.peakaboo.dataset.encoder.QOXRF;
import org.peakaboo.framework.accent.Platform;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;

public class PeakabooConfiguration {

	public static ScratchEncoder<Spectrum> spectrumEncoder = new QOXRF();
	
	public static final MemorySize memorySize = calcMemoryFootprint();

	public enum MemorySize {
		TINY, SMALL, MEDIUM, LARGE;
	}

	/**
	 * Which HDF5 reader backend the universalhdf5 data sources should use: AUTO (native when
	 * available, jhdf otherwise), NATIVE, or JHDF.
	 */
	public static String hdfBackend = System.getProperty("peakaboo.hdf.backend", "AUTO");

	private static MemorySize calcMemoryFootprint() {
		
		long maxHeap = Platform.maxHeap();
		
		
		if      (maxHeap < 512)   return MemorySize.TINY;
		else if (maxHeap <= 2048) return MemorySize.SMALL;
		else if (maxHeap <= 8192) return MemorySize.MEDIUM;
		else                      return MemorySize.LARGE;
		
		
	}
}
