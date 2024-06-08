package org.peakaboo.app;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class PeakabooConfiguration {

	public static ScratchEncoder<Spectrum> spectrumEncoder = new CompoundEncoder<>(
			Serializers.fstUnsafe(
					ArraySpectrum.class,
					float[].class
				), 
			Compressors.lz4fast()
		);

	
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
