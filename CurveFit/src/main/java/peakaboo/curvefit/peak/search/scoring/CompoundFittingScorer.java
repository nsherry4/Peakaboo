package peakaboo.curvefit.peak.search.scoring;

import java.util.HashMap;
import java.util.Map;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CompoundFittingScorer implements FittingScorer {

	private Map<FittingScorer, Float> scorers = new HashMap<>();
	
	public void add(FittingScorer scorer, float weight) {
		scorers.put(scorer, weight);
	}
	
	@Override
	public float score(TransitionSeries ts) {
		float score = 1f;
		for (FittingScorer scorer : scorers.keySet()) {
			score *= scorer.score(ts) * scorers.get(scorer);
		}
		return score;
	}

}
