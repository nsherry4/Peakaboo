package org.peakaboo.mapping.filter.plugin.plugins.sizing;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.model.MapFilter.MapFilterContext;

public class BinningMapFilterTest {

	private BinningMapFilter makeFilter() {
		BinningMapFilter filter = new BinningMapFilter();
		filter.initialize();
		return filter;
	}

	private BinningMapFilter makeFilter(int reps) {
		BinningMapFilter filter = makeFilter();
		((Parameter<Integer>) filter.getParameters().get(0)).setValue(reps);
		return filter;
	}

	private AreaMap makeMap(int width, int height) {
		return new AreaMap(new ArraySpectrum(width * height), Element.Fe, new Coord<>(width, height), null);
	}

	private AreaMap makeMap(int width, int height, float... values) {
		ArraySpectrum data = new ArraySpectrum(width * height);
		for (int i = 0; i < values.length; i++) {
			data.set(i, values[i]);
		}
		return new AreaMap(data, Element.Fe, new Coord<>(width, height), null);
	}

	@Test
	public void testNormalBinning() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(4, 4)));
		Assert.assertEquals(2, (int) result.getSize().x);
		Assert.assertEquals(2, (int) result.getSize().y);
	}

	@Test
	public void testWidthTooSmallToBin() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(1, 4)));
		Assert.assertEquals(1, (int) result.getSize().x);
		Assert.assertEquals(4, (int) result.getSize().y);
	}

	@Test
	public void testHeightTooSmallToBin() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(4, 1)));
		Assert.assertEquals(4, (int) result.getSize().x);
		Assert.assertEquals(1, (int) result.getSize().y);
	}

	@Test
	public void testBothDimensionsTooSmallToBin() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(1, 1)));
		Assert.assertEquals(1, (int) result.getSize().x);
		Assert.assertEquals(1, (int) result.getSize().y);
	}

	// A 2x2 map with known values should average to a single pixel.
	// Layout (row-major): [1, 3, 5, 7] → (1+3+5+7)/4 = 4.0
	@Test
	public void testAveragingCorrectness() {
		AreaMap source = makeMap(2, 2, 1f, 3f, 5f, 7f);
		AreaMap result = makeFilter().filter(new MapFilterContext(source));
		Assert.assertEquals(1, (int) result.getSize().x);
		Assert.assertEquals(1, (int) result.getSize().y);
		Assert.assertEquals(4.0f, result.getData().get(0), 0.0001f);
	}

	// Odd dimensions: the trailing row/column should be dropped.
	// 5x4 → 2x2, not 3x2
	@Test
	public void testOddDimensionTruncation() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(5, 4)));
		Assert.assertEquals(2, (int) result.getSize().x);
		Assert.assertEquals(2, (int) result.getSize().y);
		Assert.assertEquals(4, result.getData().size());
	}

	// 2 repetitions on an 8x8 map should yield 2x2.
	@Test
	public void testMultipleRepetitions() {
		AreaMap result = makeFilter(2).filter(new MapFilterContext(makeMap(8, 8)));
		Assert.assertEquals(2, (int) result.getSize().x);
		Assert.assertEquals(2, (int) result.getSize().y);
	}

	// After binning, the data length must match the declared dimensions.
	@Test
	public void testDataSizeMatchesDeclaredSize() {
		AreaMap result = makeFilter().filter(new MapFilterContext(makeMap(6, 4)));
		int expectedSize = result.getSize().x * result.getSize().y;
		Assert.assertEquals(expectedSize, result.getData().size());
	}

}
