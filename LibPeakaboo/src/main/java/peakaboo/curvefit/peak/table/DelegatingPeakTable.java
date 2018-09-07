package peakaboo.curvefit.peak.table;

import java.util.List;

import peakaboo.curvefit.peak.transition.TransitionSeries;

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
	public List<TransitionSeries> getAll() {
		return backing.getAll();
	}

}
