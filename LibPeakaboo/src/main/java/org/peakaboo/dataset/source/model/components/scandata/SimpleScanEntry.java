package org.peakaboo.dataset.source.model.components.scandata;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class SimpleScanEntry implements ScanEntry {
	
	private int index;
	private Spectrum spectrum;
	
	public SimpleScanEntry(int index, Spectrum spectrum) {
		this.index = index;
		this.spectrum = spectrum;
	}
	
	@Override
	public Spectrum spectrum() {
		return spectrum;
	}
	
	@Override
	public int index() {
		return index;
	}
	
}