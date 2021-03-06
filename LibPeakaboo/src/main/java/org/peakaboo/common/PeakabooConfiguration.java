package org.peakaboo.common;

import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class PeakabooConfiguration {

	public static boolean diskstore = true;
	public static ScratchEncoder<Spectrum> spectrumEncoder = new CompoundEncoder<>(Serializers.fstUnsafe(ISpectrum.class), Compressors.lz4fast());

	
	public static MemorySize memorySize = calcMemoryFootprint();

	public enum MemorySize {
		TINY, SMALL, MEDIUM, LARGE;
	}

	private static MemorySize calcMemoryFootprint() {
		
		long maxHeap = Env.maxHeap();
		
		if (maxHeap < 256) {
			return MemorySize.TINY;
		} else if (maxHeap <= 512) {
			return MemorySize.SMALL;
		} else if (maxHeap <= 1024) {
			return MemorySize.MEDIUM;
		} else {
			return MemorySize.LARGE;
		}
		
	}
		
}
