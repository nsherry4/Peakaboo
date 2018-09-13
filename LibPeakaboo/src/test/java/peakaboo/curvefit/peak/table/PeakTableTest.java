package peakaboo.curvefit.peak.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class PeakTableTest {

	@Test
	public void test() {
		
		//Xraylib has better coverage of a smaller number of elements
		PeakTable xraylib = new XrayLibPeakTable();
		Assert.assertTrue(xraylib.get(Element.H, TransitionSeriesType.K) == null);
		Assert.assertTrue(xraylib.get(Element.Au, TransitionSeriesType.K) == null);
		Assert.assertTrue(xraylib.get(Element.Fe, TransitionSeriesType.K).getTransitionCount() == 4);
		Assert.assertTrue(xraylib.get(Element.Au, TransitionSeriesType.L).getTransitionCount() == 15);
		
		
		//Kraus has poorer coverage of a smaller number of elements
		PeakTable kraus = new KrausePeakTable();
		Assert.assertTrue(kraus.get(Element.H, TransitionSeriesType.K) != null);
		Assert.assertTrue(kraus.get(Element.Au, TransitionSeriesType.K) != null);
		Assert.assertTrue(kraus.get(Element.Fe, TransitionSeriesType.K).getTransitionCount() == 3);
		Assert.assertTrue(kraus.get(Element.Au, TransitionSeriesType.L).getTransitionCount() == 5);
		
		
		//We test the combination of the two, giving per-TransitionSeries 
		//preference to xraylib when it provides one.
		PeakTable combined = new CombinedPeakTable(xraylib, kraus);
		Assert.assertTrue(combined.get(Element.H, TransitionSeriesType.K) != null);
		Assert.assertTrue(combined.get(Element.Au, TransitionSeriesType.K) != null);
		Assert.assertTrue(combined.get(Element.Fe, TransitionSeriesType.K).getTransitionCount() == 4);
		Assert.assertTrue(combined.get(Element.Au, TransitionSeriesType.L).getTransitionCount() == 15);
		
		
		//Testing convenience methods of PeakTable
		List<TransitionSeries> series = combined.getForElement(Element.Au);
		Assert.assertTrue(series.size() == 3);
		Assert.assertEquals(series.get(0).element, Element.Au);
		Assert.assertEquals(series.get(0).type, TransitionSeriesType.K);
		
		//Testing identifier string lookup
		TransitionSeries FeK = combined.get(Element.Fe, TransitionSeriesType.K);
		TransitionSeries ZnK = combined.get(Element.Zn, TransitionSeriesType.K);
		Assert.assertEquals(combined.get("Fe:K"), FeK);
		Assert.assertEquals(combined.get("Fe:K+Zn:K"), TransitionSeries.summation(FeK, ZnK));
		
	}
	
}
