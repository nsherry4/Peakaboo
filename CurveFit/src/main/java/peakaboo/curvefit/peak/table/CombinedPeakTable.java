package peakaboo.curvefit.peak.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CombinedPeakTable implements PeakTable {

	List<PeakTable> members;
	Set<TransitionSeries> series = new LinkedHashSet<>();
	
	public CombinedPeakTable(PeakTable... members) {
		this.members = new ArrayList<>(Arrays.asList(members));
		
		for (PeakTable member : members) {
			series.addAll(member.getAll());
		}
	}

	@Override
	public Collection<TransitionSeries> getAll() {
		return new ArrayList<>(series);
	}
	
	public static void main(String[] args) {
		
		
		PeakTable table;
		//table = new XrayLibPeakTable();
		table = new KrausPeakTable();
		//table = new CombinedPeakTable(new XrayLibPeakTable(), new KrausPeakTable());

		
		TransitionSeries ts;
		ts = table.get(Element.Au, TransitionSeriesType.K);
		System.out.println(ts);
		ts = table.get(Element.Fe, TransitionSeriesType.K);
		System.out.println(ts.getTransitionCount());
		ts = table.get(Element.Au, TransitionSeriesType.L);
		System.out.println(ts.getTransitionCount());
		
		
		
	}
	
}
