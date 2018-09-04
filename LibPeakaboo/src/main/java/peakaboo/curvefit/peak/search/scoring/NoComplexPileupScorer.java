package peakaboo.curvefit.peak.search.scoring;

import peakaboo.curvefit.peak.transition.TransitionSeries;

/**
 * Penalizes fittings which are too complex, composed of too many base {@link TransitionSeries}
 * @author NAS
 *
 */
public class NoComplexPileupScorer implements FittingScorer {

	@Override
	public float score(TransitionSeries ts) {
		int count = ts.getBaseTransitionSeries().size();
		switch (count) {
			case 1: return 1f;
			case 2: return 0.98f;
			case 3: return 0.25f;
			case 4: return 0.01f;
			default: return 0f;
		}
	}

}
