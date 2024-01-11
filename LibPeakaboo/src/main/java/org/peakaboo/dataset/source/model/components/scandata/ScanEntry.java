package org.peakaboo.dataset.source.model.components.scandata;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public interface ScanEntry {

	Spectrum spectrum();

	int index();

}