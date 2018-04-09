package peakaboo.curvefit.scoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import peakaboo.curvefit.transition.TransitionSeries;

public class CompoundScorer implements Scorer {

	private List<Scorer> scorers;
	
	public CompoundScorer(Scorer...scorers) {
		this.scorers = new ArrayList<>(Arrays.asList(scorers));
	}

	@Override
	public float score(TransitionSeries ts) {
		float score = 1;
		for (Scorer scorer : scorers) {
			score *= scorer.score(ts);
		}
		return score;
	}
	
	
	
}
