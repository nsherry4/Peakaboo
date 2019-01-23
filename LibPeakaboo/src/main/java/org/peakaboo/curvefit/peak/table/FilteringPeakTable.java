package org.peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

/**
 * Presents a view of a peak table which screens out certain elements. 
 * This can be used to hide elements which are outside of the detection 
 * range, or which are extremely rare and unlikely.
 * @author NAS
 *
 */
public class FilteringPeakTable implements PeakTable {

	private List<PrimaryTransitionSeries> all = null;
	private PeakTable backing;
	
	private Set<Element> filteredElements = new LinkedHashSet<>();
	//TODO: This causes eager loading of backing peak table by having to look up TransitionSeries
	private Set<ITransitionSeries> filteredTransitionSeries = new LinkedHashSet<>();
	
	public FilteringPeakTable(PeakTable backing) {
		this.backing = backing;
	}
	
	public FilteringPeakTable filter(Element e) {
		filteredElements.add(e);
		all = null;
		return this;
	}
	
	public FilteringPeakTable filter(Element e, TransitionShell tst) {
		filteredTransitionSeries.add(backing.get(e, tst));
		all = null;
		return this;
	}
	
	public FilteringPeakTable filter(ITransitionSeries filtered) {
		filteredTransitionSeries.add(filtered);
		all = null;
		return this;
	}
	
	@Override
	public synchronized List<PrimaryTransitionSeries> getAll() {
		if (all == null) {
			all = backing.getAll()
					.stream()
					.filter(ts -> !filteredElements.contains(ts.getElement()))
					.filter(ts -> !filteredTransitionSeries.contains(ts))
					.collect(Collectors.toList());
		}
		

		List<PrimaryTransitionSeries> copy = new ArrayList<>();
		for (PrimaryTransitionSeries ts : all) {
			copy.add(new PrimaryTransitionSeries(ts));
		}
		return copy;
			
		
	}

}
