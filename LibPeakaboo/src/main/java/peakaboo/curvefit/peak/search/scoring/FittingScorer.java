package peakaboo.curvefit.peak.search.scoring;

import java.util.Comparator;

import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

/**
 * Interface for scoring how well a {@link LegacyTransitionSeries} matches a spectrum.
 * Larger scores represent better fits.
 * @author NAS
 *
 */
public interface FittingScorer extends Comparator<LegacyTransitionSeries> {

	float score(LegacyTransitionSeries ts);
	
	default int compare(LegacyTransitionSeries o1, LegacyTransitionSeries o2) {
		Float s1 = score(o1);
		Float s2 = score(o2);
		return -s1.compareTo(s2);
	}

}
