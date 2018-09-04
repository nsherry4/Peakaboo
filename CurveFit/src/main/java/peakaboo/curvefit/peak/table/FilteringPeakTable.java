package peakaboo.curvefit.peak.table;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

/**
 * Presents a view of a peak table which screens out certain elements. 
 * This can be used to hide elements which are outside of the detection 
 * range, or which are extremely rare and unlikely.
 * @author NAS
 *
 */
public class FilteringPeakTable implements PeakTable {

	private List<TransitionSeries> all = null;
	private PeakTable backing;
	
	private Set<Element> filteredElements = new LinkedHashSet<>();
	private Set<TransitionSeries> filteredTransitionSeries = new LinkedHashSet<>();
	
	public FilteringPeakTable(PeakTable backing) {
		this.backing = backing;
	}
	
	public FilteringPeakTable filter(Element e) {
		filteredElements.add(e);
		all = null;
		return this;
	}
	
	public FilteringPeakTable filter(Element e, TransitionSeriesType tst) {
		filteredTransitionSeries.add(backing.get(e, tst));
		all = null;
		return this;
	}
	
	public FilteringPeakTable filter(TransitionSeries filtered) {
		filteredTransitionSeries.add(filtered);
		all = null;
		return this;
	}
	
	@Override
	public synchronized List<TransitionSeries> getAll() {
		if (all == null) {
			all = backing.getAll()
					.stream()
					.filter(ts -> !filteredElements.contains(ts.element))
					.filter(ts -> !filteredTransitionSeries.contains(ts))
					.collect(Collectors.toList());
		}
		return all;
	}

}
