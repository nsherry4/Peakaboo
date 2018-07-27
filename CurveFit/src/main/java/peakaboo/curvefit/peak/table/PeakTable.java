package peakaboo.curvefit.peak.table;

import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public interface PeakTable {

	static final DelegatingPeakTable SYSTEM = new DelegatingPeakTable(
			new FilteringPeakTable(
				new CombinedPeakTable(
						new XrayLibPeakTable(), 
						new KrausePeakTable()
				),
				Element.H,
				Element.He,
				Element.Li,
				Element.Ne,
				Element.Kr,
				Element.Tc,
				Element.Xe,
				Element.Po,
				Element.At,
				Element.Rn,
				Element.Fr,
				Element.Ra,
				Element.Pa,
				Element.Np,
				Element.Pu,
				Element.Am,
				Element.Cm,
				Element.Bk,
				Element.Cf,
				Element.Es,
				Element.Fm,
				Element.Nb
			)
	);
	
	default TransitionSeries get(Element e, TransitionSeriesType tst) {
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
	
	List<TransitionSeries> getAll();
	
	default List<TransitionSeries> getForElement(Element e) {
		return getAll()
				.stream()
				.filter(ts -> (ts.element == e))
				.collect(Collectors.toList());
	}
	
	
}
