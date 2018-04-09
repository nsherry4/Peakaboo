package peakaboo.curvefit.scoring;

import java.util.Comparator;

import peakaboo.curvefit.transition.TransitionSeries;

public interface Scorer extends Comparator<TransitionSeries> {

	float score(TransitionSeries ts);
	
	default int compare(TransitionSeries o1, TransitionSeries o2) {
		Float s1 = score(o1);
		Float s2 = score(o2);
		return -s1.compareTo(s2);
	}

}
