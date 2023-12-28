package org.peakaboo.curvefit.peak.search.searcher;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public interface PeakSearcher {

	List<Integer> search(SpectrumView data);
	
}
