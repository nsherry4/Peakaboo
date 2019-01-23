package org.peakaboo.curvefit.peak.search.searcher;

import java.util.List;

import cyclops.ReadOnlySpectrum;

public interface PeakSearcher {

	List<Integer> search(ReadOnlySpectrum data);
	
}
