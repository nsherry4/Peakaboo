package peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CombinedPeakTable implements PeakTable {

	List<TransitionSeries> series = new ArrayList<>();
	
	public CombinedPeakTable(PeakTable... members) {
		
		Set<TransitionSeries> merged = new HashSet<>();
		for (PeakTable member : members) {
			merged.addAll(member.getAll());
		}
		
		series.addAll(merged);
		
		series.sort((t1, t2) -> {
			int shellDiff = t1.type.shell() - t2.type.shell();
			if (shellDiff != 0) {
				return shellDiff;
			}
			
			int zDiff = t1.element.atomicNumber() - t2.element.atomicNumber();
			return zDiff;
			
		});
		
	}

	@Override
	public List<TransitionSeries> getAll() {
		
		List<TransitionSeries> copy = new ArrayList<>();
		for (TransitionSeries ts : series) {
			copy.add(new TransitionSeries(ts));
		}
		return copy;
		
	}
	
}
