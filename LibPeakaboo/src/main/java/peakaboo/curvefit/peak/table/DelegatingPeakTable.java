package peakaboo.curvefit.peak.table;

import java.util.List;

import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

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
	public List<LegacyTransitionSeries> getAll() {
		return backing.getAll();
	}

}
