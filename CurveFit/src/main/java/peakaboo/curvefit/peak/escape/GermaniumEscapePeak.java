package peakaboo.curvefit.peak.escape;

import java.util.List;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class GermaniumEscapePeak implements EscapePeak {

	@Override
	public boolean hasOffset() {
		return true;
	}

	@Override
	public List<Transition> offset() {
		return PeakTable.SYSTEM.get(Element.Ge, TransitionSeriesType.K).getAllTransitions();
	}

	@Override
	public float energyGap() {
		return 0.0029f;
	}

	@Override
	public float fanoFactor() {
		return 0.13f;
	}

	@Override
	public String pretty() {
		return "Germanium";
	}

	@Override
	public String toString() {
		return pretty();
	}

	@Override
	public EscapePeakType type() {
		return EscapePeakType.GERMANIUM;
	}
	
}
