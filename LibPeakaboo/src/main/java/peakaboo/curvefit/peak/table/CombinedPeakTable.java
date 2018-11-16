package peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CombinedPeakTable implements PeakTable {

	List<TransitionSeries> series;
	PeakTable[] members;
	
	public CombinedPeakTable(PeakTable... members) {
		this.members = members;
	}
	
	private void load() {
		series = new ArrayList<>();
		
		Set<TransitionSeries> merged = new HashSet<>();
		for (PeakTable member : members) {
			//add if not already present
			merged.addAll(member.getAll());
		}
		
		series.addAll(merged);
		
		series.sort((t1, t2) -> {
			int shellDiff = t1.getShell().shell() - t2.getShell().shell();
			if (shellDiff != 0) {
				return shellDiff;
			}
			
			int zDiff = t1.getElement().atomicNumber() - t2.getElement().atomicNumber();
			return zDiff;
			
		});
		
	}

	@Override
	public List<TransitionSeries> getAll() {
		if (series == null) {
			load();
		}
		
		List<TransitionSeries> copy = new ArrayList<>();
		for (TransitionSeries ts : series) {
			copy.add(new TransitionSeries(ts));
		}
		return copy;
		
	}
	
}
