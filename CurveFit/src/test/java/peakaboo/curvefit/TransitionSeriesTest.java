package peakaboo.curvefit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesMode;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;

public class TransitionSeriesTest {

	@Test
	public void equality() {
		TransitionSeries ask1 = PeakTable.getTransitionSeries(Element.As, TransitionSeriesType.K);
		TransitionSeries ask2 = PeakTable.getTransitionSeries(Element.As, TransitionSeriesType.K);
		
		
		Assert.assertEquals(ask1, ask2);
		Map<TransitionSeries, Boolean> map = new HashMap<>();
		map.put(ask1, true);
		Assert.assertTrue(map.containsKey(ask1));
		Assert.assertTrue(map.containsKey(ask2));
		
		
	}
	
	
	
}
