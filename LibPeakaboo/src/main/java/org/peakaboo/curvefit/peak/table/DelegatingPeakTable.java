package org.peakaboo.curvefit.peak.table;

import java.util.List;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;

public class DelegatingPeakTable implements PeakTable {

	private PeakTable backing;
	
	public DelegatingPeakTable(PeakTable backing) {
		this.backing = backing;
	}
	
	public void setSource(PeakTable table) {
		this.backing = table;
	}
	
	public PeakTable getSource() {
		return backing;
	}
	
	@Override
	public List<PrimaryTransitionSeries> getAll() {
		return backing.getAll();
	}

}
