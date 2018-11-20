package peakaboo.curvefit.peak.transition;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.KrausePeakTable;
import peakaboo.curvefit.peak.table.PeakTable;

public class TransitionSeriesTest {

	@Test
	public void equality() {
		
		PeakTable table = new KrausePeakTable();
		
		LegacyTransitionSeries ask1 = table.get(Element.As, TransitionShell.K);
		ITransitionSeries ask2 = table.get(Element.As, TransitionShell.K);
		ITransitionSeries ask3 = table.get(Element.As, TransitionShell.L);
		ITransitionSeries ask4 = table.get(Element.Fe, TransitionShell.K);
		
		
		Assert.assertEquals(ask1, ask2);
		Assert.assertFalse(ask2.equals(ask3));
		Assert.assertFalse(ask2.equals(ask4));
		Assert.assertFalse(ask3.equals(ask4));
		
		Map<LegacyTransitionSeries, Boolean> map = new HashMap<>();
		map.put(ask1, true);
		Assert.assertTrue(map.containsKey(ask1));
		Assert.assertTrue(map.containsKey(ask2));
		
		ITransitionSeries id1 = LegacyTransitionSeries.get("Fe:K");
		ITransitionSeries id2 = LegacyTransitionSeries.get("Zn:K");
		Assert.assertFalse(id1.equals(id2));
		Assert.assertEquals(id1, new LegacyTransitionSeries(Element.Fe, TransitionShell.K));
		Assert.assertEquals(id2, new LegacyTransitionSeries(Element.Zn, TransitionShell.K));
		
		ITransitionSeries ids = LegacyTransitionSeries.get("Fe:K+Zn:K");
		Assert.assertEquals(ids, LegacyTransitionSeries.summation(id1, id2));
		
		
		
	}
	
	
	
}
