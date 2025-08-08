package org.peakaboo.curvefit.peak.search.scoring;

import java.util.HashMap;
import java.util.Map;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class CompoundFittingScorer implements FittingScorer {

	private Map<FittingScorer, Float> weights = new HashMap<>();
	private float totalWeight = 0f;
	
	public void add(FittingScorer scorer, float weight) {
		weights.put(scorer, weight);
		totalWeight += weight;
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		float score = 1f;
		for (FittingScorer scorer : weights.keySet()) {
			float weight = weights.get(scorer);
			float thisScore = scorer.score(ts);
			if (Float.isNaN(thisScore)) {
				thisScore = 0;
			}
			score *= thisScore * (weight / totalWeight);
		}
		return score;
	}

}
