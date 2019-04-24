package org.peakaboo.curvefit.peak.search.searcher;

import java.util.List;

import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

public interface PeakSearcher {

	List<Integer> search(ReadOnlySpectrum data);
	
}
