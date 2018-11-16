package peakaboo.curvefit.peak.escape;

import java.util.List;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class SiliconEscapePeak implements EscapePeak {

	@Override
	public boolean hasOffset() {
		return true;
	}

	@Override
	public List<Transition> offset() {
		return transitionSeries().getAllTransitions();
	}

	@Override
	public float energyGap() {
		// TODO Auto-generated method stub
		return 0.00358f;
	}

	@Override
	public float fanoFactor() {
		return 0.144f;
	}

	@Override
	public String pretty() {
		return "Silicon";
	}
	
	@Override
	public String toString() {
		return pretty();
	}

	@Override
	public EscapePeakType type() {
		return EscapePeakType.SILICON;
	}
	
	@Override
	public LegacyTransitionSeries transitionSeries() {
		// TODO Auto-generated method stub
		return PeakTable.SYSTEM.get(Element.Si, TransitionShell.K);
	}
	
}
