package peakaboo.curvefit.peak.table;

import java.util.Arrays;
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
	
	default TransitionSeries get(String identifier) {
		if (identifier.contains("+")) {
			List<TransitionSeries> tss = Arrays.asList(identifier.split("\\+")).stream().map(this::get).collect(Collectors.toList());
			if (tss.contains(null)) {
				throw new RuntimeException("Poorly formated TransitionSeries identifier string: " + identifier);
			}
			return TransitionSeries.summation(tss);
		}
		String[] parts = identifier.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		Element e = Element.valueOf(parts[0]);
		TransitionSeriesType tst = TransitionSeriesType.fromTypeString(parts[1].trim());
		if (e == null || tst == null) {
			return null;
		}
		return get(e, tst);
	}
	
	List<TransitionSeries> getAll();
	
	default List<TransitionSeries> getForElement(Element e) {
		return getAll()
				.stream()
				.filter(ts -> (ts.element == e))
				.collect(Collectors.toList());
	}
	
	
}
