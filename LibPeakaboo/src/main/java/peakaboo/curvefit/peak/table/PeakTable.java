package peakaboo.curvefit.peak.table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public interface PeakTable {

	static final DelegatingPeakTable SYSTEM = new DelegatingPeakTable(
			new FilteringPeakTable(
				new CombinedPeakTable(
						new XrayLibPeakTable(), 
						new KrausePeakTable()
				)
			)
			.filter(Element.H)
			.filter(Element.He)
			.filter(Element.Li)
			.filter(Element.Ne)
			.filter(Element.Kr)
			.filter(Element.Tc)
			.filter(Element.Xe)
			.filter(Element.Po)
			.filter(Element.At)
			.filter(Element.Rn)
			.filter(Element.Fr)
			.filter(Element.Ra)
			.filter(Element.Pa)
			.filter(Element.Np)
			.filter(Element.Pu)
			.filter(Element.Am)
			.filter(Element.Cm)
			.filter(Element.Bk)
			.filter(Element.Cf)
			.filter(Element.Es)
			.filter(Element.Fm)
			.filter(Element.Ac)
			.filter(Element.F)
	);
	
	default TransitionSeries get(Element e, TransitionShell tst) {
		List<TransitionSeries> tss = getAll()
				.stream()
				.filter(ts -> (ts.element == e) && (ts.type == tst))
				.collect(Collectors.toList());
		if (tss.size() == 0) return null;
		if (tss.size() > 1) {
			throw new RuntimeException("Found more than one TransitionSeries for the given Element and TransitionSeriesType");
		}
		return tss.get(0);
	}
	
	/**
	 * Gets the equivalent TransitionSeries from this PeakTable. This is a
	 * convenience method for {@link PeakTable#get(Element, TransitionShell)}
	 */
	default TransitionSeries get(TransitionSeries other) {
		if (other.type == TransitionShell.COMPOSITE) {
			List<TransitionSeries> members = other.getBaseTransitionSeries().stream().map(ts -> get(ts.element, ts.type)).filter(ts -> ts != null).collect(Collectors.toList());
			return TransitionSeries.summation(members);
		} else {
			return get(other.element, other.type);
		}
	}
	
	/**
	 * Reads the identifier string and returns the appropriate TransitionSeries from
	 * the PeakTable, or null if the identifier string is invalid, or the
	 * TransitionSeries is not listed in this PeakTable.
	 */
	default TransitionSeries get(String identifier) {
		TransitionSeries ts = TransitionSeries.get(identifier);
		if (ts == null) {
			return null;
		}
		return get(ts);
	}
	
	List<TransitionSeries> getAll();
	
	default List<TransitionSeries> getForElement(Element e) {
		return getAll()
				.stream()
				.filter(ts -> (ts.element == e))
				.collect(Collectors.toList());
	}
	
	
}
