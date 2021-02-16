package org.peakaboo.curvefit.peak.table;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

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
			.filter(Element.At)
			.filter(Element.Rn)
			.filter(Element.Fr)
			.filter(Element.Am)
			.filter(Element.Cm)
			.filter(Element.Bk)
			.filter(Element.Cf)
			.filter(Element.Es)
			.filter(Element.Fm)
			.filter(Element.F)
	);
	
	default PrimaryTransitionSeries get(Element e, TransitionShell tst) {
		List<PrimaryTransitionSeries> tss = getAll()
				.stream()
				.filter(ts -> (ts.getElement() == e) && (ts.getShell() == tst))
				.collect(Collectors.toList());
		if (tss.isEmpty()) return null;
		if (tss.size() > 1) {
			throw new RuntimeException("Found more than one TransitionSeries for the given Element and TransitionSeriesType");
		}
		return tss.get(0);
	}
	
	/**
	 * Gets the equivalent TransitionSeries from this PeakTable. This is a
	 * convenience method for {@link PeakTable#get(Element, TransitionShell)}
	 */
	default ITransitionSeries get(ITransitionSeries other) {
		if (other.getShell() == TransitionShell.COMPOSITE) {
			List<ITransitionSeries> members = other.getPrimaryTransitionSeries().stream()
					.map(ts -> get(ts.getElement(), ts.getShell()))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			return ITransitionSeries.pileup(members);
		} else {
			return get(other.getElement(), other.getShell());
		}
	}
	
	/**
	 * Reads the identifier string and returns the appropriate TransitionSeries from
	 * the PeakTable, or null if the identifier string is invalid, or the
	 * TransitionSeries is not listed in this PeakTable.
	 */
	default ITransitionSeries get(String identifier) {
		ITransitionSeries ts = ITransitionSeries.get(identifier);
		if (ts == null) {
			return null;
		}
		return get(ts);
	}
	
	List<PrimaryTransitionSeries> getAll();
	
	default List<PrimaryTransitionSeries> getForElement(Element e) {
		return getAll()
				.stream()
				.filter(ts -> (ts.getElement() == e))
				.collect(Collectors.toList());
	}
	
	
}
