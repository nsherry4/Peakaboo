package org.peakaboo.controller.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SyntheticDataSource;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;
import org.peakaboo.framework.autodialog.model.Parameter;

public class MappingControllerFilteringTest {

	private MappingController mc;

	@BeforeClass
	public static void initRegistries() {
		CurveFitterRegistry.init();
		ChannelViewModeRegistry.init();
		FittingFunctionRegistry.init();
		FittingSolverRegistry.init();
		FilterRegistry.init();
		MapFilterRegistry.init();
	}

	@Before
	public void setUp() {
		PlotController pc = new PlotController(null);
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(25, 2048, 1000f);
		pc.data().setDataSource(ds, null, () -> false);
		mc = SyntheticMapData.create(pc, 5, 5);
	}

	@SuppressWarnings("unchecked")
	private MultiplyMapFilter createMultiplyFilter(float multiplier) {
		MultiplyMapFilter filter = new MultiplyMapFilter();
		filter.initialize();
		((Parameter<Float>) filter.getParameters().get(0)).setValue(multiplier);
		return filter;
	}

	@Test
	public void testNoFiltersInitially() {
		assertEquals(0, mc.getFiltering().size());
	}

	@Test
	public void testSummedMapNonNull() {
		AreaMap summed = mc.getFiltering().getSummedMap();
		assertNotNull(summed);
		assertEquals(5L, (long) summed.getSize().x);
		assertEquals(5L, (long) summed.getSize().y);
	}

	@Test
	public void testAddFilter() {
		mc.getFiltering().add(createMultiplyFilter(2f));
		assertEquals(1, mc.getFiltering().size());
	}

	@Test
	public void testRemoveFilter() {
		MapFilter f = createMultiplyFilter(2f);
		mc.getFiltering().add(f);
		assertEquals(1, mc.getFiltering().size());
		mc.getFiltering().remove(f);
		assertEquals(0, mc.getFiltering().size());
	}

	@Test
	public void testFilterChangesData() {
		AreaMap before = mc.getFiltering().getSummedMap();
		float sumBefore = sumOf(before.getData());

		mc.getFiltering().add(createMultiplyFilter(2f));

		AreaMap after = mc.getFiltering().getSummedMap();
		float sumAfter = sumOf(after.getData());

		// With a 2x multiplier, the sum should roughly double
		assertTrue("Filtered sum should be greater", sumAfter > sumBefore * 1.5f);
	}

	@Test
	public void testFilterFiresEvent() {
		List<MapUpdateType> received = new ArrayList<>();
		mc.addListener(received::add);
		mc.getFiltering().add(createMultiplyFilter(2f));
		assertTrue("Expected FILTER event", received.contains(MapUpdateType.FILTER));
	}

	@Test
	public void testFilterEnabledDisabled() {
		AreaMap original = mc.getFiltering().getSummedMap();
		float originalSum = sumOf(original.getData());

		mc.getFiltering().add(createMultiplyFilter(3f));
		float filteredSum = sumOf(mc.getFiltering().getSummedMap().getData());
		assertTrue("Filter should change data", Math.abs(filteredSum - originalSum) > 1f);

		// Disable the filter
		mc.getFiltering().setMapFilterEnabled(0, false);
		float disabledSum = sumOf(mc.getFiltering().getSummedMap().getData());
		assertEquals("Disabled filter should restore data", originalSum, disabledSum, 0.01f);

		// Re-enable
		mc.getFiltering().setMapFilterEnabled(0, true);
		float reenabledSum = sumOf(mc.getFiltering().getSummedMap().getData());
		assertEquals("Re-enabled filter should reapply", filteredSum, reenabledSum, 0.01f);
	}

	@Test
	public void testIsFilteringFlag() {
		assertFalse(mc.getFiltering().isFiltering());

		mc.getFiltering().add(createMultiplyFilter(2f));
		assertTrue(mc.getFiltering().isFiltering());

		mc.getFiltering().setMapFilterEnabled(0, false);
		assertFalse(mc.getFiltering().isFiltering());
	}

	@Test
	public void testClearFilters() {
		mc.getFiltering().add(createMultiplyFilter(2f));
		mc.getFiltering().add(createMultiplyFilter(3f));
		assertEquals(2, mc.getFiltering().size());
		mc.getFiltering().clear();
		assertEquals(0, mc.getFiltering().size());
	}

	private float sumOf(SpectrumView spectrum) {
		float sum = 0;
		for (int i = 0; i < spectrum.size(); i++) {
			sum += spectrum.get(i);
		}
		return sum;
	}

}
