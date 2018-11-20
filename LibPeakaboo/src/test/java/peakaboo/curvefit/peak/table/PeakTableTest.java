package peakaboo.curvefit.peak.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.PileUpTransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class PeakTableTest {

	@Test
	public void test() {
		
		//Xraylib has better coverage of a smaller number of elements
		PeakTable xraylib = new XrayLibPeakTable();
		Assert.assertTrue(xraylib.get(Element.H, TransitionShell.K) == null);
		Assert.assertTrue(xraylib.get(Element.Au, TransitionShell.K) == null);
		Assert.assertTrue(xraylib.get(Element.Fe, TransitionShell.K).getTransitionCount() == 4);
		Assert.assertTrue(xraylib.get(Element.Au, TransitionShell.L).getTransitionCount() == 15);
		
		
		//Kraus has poorer coverage of a smaller number of elements
		PeakTable kraus = new KrausePeakTable();
		Assert.assertTrue(kraus.get(Element.H, TransitionShell.K) != null);
		Assert.assertTrue(kraus.get(Element.Au, TransitionShell.K) != null);
		Assert.assertTrue(kraus.get(Element.Fe, TransitionShell.K).getTransitionCount() == 3);
		Assert.assertTrue(kraus.get(Element.Au, TransitionShell.L).getTransitionCount() == 5);
		
		
		//We test the combination of the two, giving per-TransitionSeries 
		//preference to xraylib when it provides one.
		PeakTable combined = new CombinedPeakTable(xraylib, kraus);
		Assert.assertTrue(combined.get(Element.H, TransitionShell.K) != null);
		Assert.assertTrue(combined.get(Element.Au, TransitionShell.K) != null);
		Assert.assertTrue(combined.get(Element.Fe, TransitionShell.K).getTransitionCount() == 4);
		Assert.assertTrue(combined.get(Element.Au, TransitionShell.L).getTransitionCount() == 15);
		
		
		//Testing convenience methods of PeakTable
		List<? extends ITransitionSeries> series = combined.getForElement(Element.Au);
		Assert.assertTrue(series.size() == 3);
		Assert.assertEquals(series.get(0).getElement(), Element.Au);
		Assert.assertEquals(series.get(0).getShell(), TransitionShell.K);
		
		//Testing identifier string lookup
		ITransitionSeries FeK = combined.get(Element.Fe, TransitionShell.K);
		ITransitionSeries ZnK = combined.get(Element.Zn, TransitionShell.K);
		Assert.assertEquals(combined.get("Fe:K"), FeK);
		
		ITransitionSeries pu1, pu2;
		pu1 = combined.get("Fe:K+Zn:K");
		pu2 = ITransitionSeries.pileup(FeK, ZnK);
		Assert.assertTrue(pu1.equals(pu2));
		Assert.assertEquals(pu1, pu2);
		
	}
	
}
