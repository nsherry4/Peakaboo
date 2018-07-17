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
		return 1000f / (999f+(float) Math.pow(count, 3));
	}

}
