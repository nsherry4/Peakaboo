package org.peakaboo.datasource;

import org.junit.Test;
import org.peakaboo.dataset.source.model.components.physicalsize.SimplePhysicalSize;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SISize;

import junit.framework.Assert;

public class SimplePhysicalSizeTests {

	@Test
	public void main() {
		
		SimplePhysicalSize s = new SimplePhysicalSize(SISize.mm);
		
		s.putPoint(5, new Coord<>(1, 1));
		Assert.assertEquals(s.getPhysicalCoordinatesAtIndex(5), new Coord<>(1, 1));
		
		s.putPoint(10, new Coord<>(2, 2));
		Assert.assertEquals(s.getPhysicalDimensions().x.end, 2f);
		
	}
	
}
