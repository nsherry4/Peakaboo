package peakaboo.curvefit.peak.table;

import java.util.List;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public class DelegatingPeakTable implements PeakTable {

	private PeakTable backing;
	
	public void setSource(PeakTable table) {
		this.backing = table;
	}
	
	@Override
	public List<TransitionSeries> getAll() {
		return backing.getAll();
	}

}
