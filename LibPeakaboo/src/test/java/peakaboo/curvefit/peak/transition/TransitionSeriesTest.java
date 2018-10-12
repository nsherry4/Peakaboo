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
		
		TransitionSeries ask1 = table.get(Element.As, TransitionSeriesType.K);
		TransitionSeries ask2 = table.get(Element.As, TransitionSeriesType.K);
		TransitionSeries ask3 = table.get(Element.As, TransitionSeriesType.L);
		TransitionSeries ask4 = table.get(Element.Fe, TransitionSeriesType.K);
		
		
		Assert.assertEquals(ask1, ask2);
		Assert.assertFalse(ask2.equals(ask3));
		Assert.assertFalse(ask2.equals(ask4));
		Assert.assertFalse(ask3.equals(ask4));
		
		Map<TransitionSeries, Boolean> map = new HashMap<>();
		map.put(ask1, true);
		Assert.assertTrue(map.containsKey(ask1));
		Assert.assertTrue(map.containsKey(ask2));
		
		TransitionSeries id1 = TransitionSeries.get("Fe:K");
		TransitionSeries id2 = TransitionSeries.get("Zn:K");
		Assert.assertFalse(id1.equals(id2));
		Assert.assertEquals(id1, new TransitionSeries(Element.Fe, TransitionSeriesType.K));
		Assert.assertEquals(id2, new TransitionSeries(Element.Zn, TransitionSeriesType.K));
		
		TransitionSeries ids = TransitionSeries.get("Fe:K+Zn:K");
		Assert.assertEquals(ids, TransitionSeries.summation(id1, id2));
		
		
		
	}
	
	
	
}
