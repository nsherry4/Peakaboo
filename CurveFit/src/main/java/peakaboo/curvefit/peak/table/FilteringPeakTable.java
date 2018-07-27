package peakaboo.curvefit.peak.table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.transition.TransitionSeries;

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
	private List<Element> elements;
	
	public FilteringPeakTable(PeakTable backing, Element...elements) {
		this.backing = backing;
		this.elements = Arrays.asList(elements);
	}
	
	@Override
	public synchronized List<TransitionSeries> getAll() {
		if (all == null) {
			all = backing.getAll().stream().filter(ts -> !elements.contains(ts.element)).collect(Collectors.toList());
		}
		return all;
	}

}
