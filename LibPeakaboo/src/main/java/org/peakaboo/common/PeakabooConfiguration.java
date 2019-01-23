package org.peakaboo.common;

import cyclops.ISpectrum;
import cyclops.Spectrum;
import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.CompoundEncoder;
import net.sciencestudio.scratch.encoders.compressors.Compressors;
import net.sciencestudio.scratch.encoders.serializers.Serializers;

public class PeakabooConfiguration {

	public static boolean diskstore = true;
	public static ScratchEncoder<Spectrum> spectrumEncoder = new CompoundEncoder<>(Serializers.fstUnsafe(ISpectrum.class), Compressors.lz4fast());

	
	public static MemorySize memorySize = calcMemoryFootprint();

	public enum MemorySize {
		TINY, SMALL, MEDIUM, LARGE;
	}

	private static MemorySize calcMemoryFootprint() {
		
		long maxHeap = Env.maxHeap();
		
		if (maxHeap < 128) {
			return MemorySize.TINY;
		} else if (maxHeap < 256) {
			return MemorySize.SMALL;
		} else if (maxHeap < 1024) {
			return MemorySize.MEDIUM;
		} else {
			return MemorySize.LARGE;
		}
		
	}
		
}
