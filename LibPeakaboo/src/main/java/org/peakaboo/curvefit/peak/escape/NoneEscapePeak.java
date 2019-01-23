package peakaboo.curvefit.peak.escape;

import java.util.Collections;
import java.util.List;

import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.Transition;

public class NoneEscapePeak implements EscapePeak {

	@Override
	public boolean hasOffset() {
		return false;
	}

	@Override
	public List<Transition> offset() {
		return Collections.emptyList();
	}

	@Override
	public float energyGap() {
		return 0;
	}

	@Override
	public float fanoFactor() {
		return 0;
	}

	@Override
	public String pretty() {
		return "None";
	}
	
	@Override
	public String toString() {
		return pretty();
	}
	
	@Override
	public EscapePeakType type() {
		return EscapePeakType.NONE;
	}
	
	@Override
	public ITransitionSeries transitionSeries() {
		return null;
	}

}
