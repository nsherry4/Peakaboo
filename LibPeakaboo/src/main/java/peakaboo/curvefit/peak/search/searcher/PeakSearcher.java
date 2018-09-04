package peakaboo.curvefit.peak.search.searcher;

import java.util.List;

import scitypes.ReadOnlySpectrum;

public interface PeakSearcher {

	List<Integer> search(ReadOnlySpectrum data);
	
}
