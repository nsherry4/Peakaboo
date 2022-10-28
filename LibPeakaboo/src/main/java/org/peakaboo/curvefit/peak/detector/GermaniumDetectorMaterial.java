package org.peakaboo.curvefit.peak.detector;

import java.util.List;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

public class GermaniumDetectorMaterial implements DetectorMaterial {

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
		return 0.0029f;
	}

	@Override
	public float fanoFactor() {
		return 0.13f;
	}

	@Override
	public String name() {
		return "Germanium";
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public DetectorMaterialType type() {
		return DetectorMaterialType.GERMANIUM;
	}

	@Override
	public ITransitionSeries transitionSeries() {
		return PeakTable.SYSTEM.get(Element.Ge, TransitionShell.K);
	}
	
}
