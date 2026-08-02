package org.peakaboo.controller.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.peakaboo.datalabel.DataLabel;
import org.peakaboo.datalabel.DataScope;
import org.peakaboo.datalabel.DataTag;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.FastAverageMapFilter;

public class MappingControllerDataTagTest {

	private static final DataTag PLOT_SMOOTHED = new DataTag(DataScope.PLOT, DataLabel.SMOOTHED);
	private static final DataTag PLOT_BACKGROUND = new DataTag(DataScope.PLOT, DataLabel.BACKGROUND_REMOVED);
	private static final DataTag MAP_SMOOTHED = new DataTag(DataScope.MAP, DataLabel.SMOOTHED);

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

	private MapFilter createSmoothingFilter() {
		FastAverageMapFilter filter = new FastAverageMapFilter();
		filter.initialize();
		return filter;
	}

	private MapFilter createMultiplyFilter() {
		MultiplyMapFilter filter = new MultiplyMapFilter();
		filter.initialize();
		return filter;
	}

	@Test
	public void testNoTagsInitially() {
		assertTrue(mc.getFiltering().getDataTags().isEmpty());
		assertNull(mc.getFiltering().getActionDescription());
	}

	@Test
	public void testMapFilterTags() {
		//Map-stage processing is implied on a map, so it reads without a prefix
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals(List.of(MAP_SMOOTHED), mc.getFiltering().getDataTags());
		assertEquals("Smoothed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testInheritedSourceTags() {
		mc.rawDataController.getMapResultSet().setSourceTags(List.of(PLOT_BACKGROUND));
		assertEquals(List.of(PLOT_BACKGROUND), mc.getFiltering().getDataTags());
		assertEquals("Plot: Background Removed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testInheritedAndMapFilterTagsCombine() {
		mc.rawDataController.getMapResultSet().setSourceTags(List.of(PLOT_BACKGROUND));
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals("Smoothed; Plot: Background Removed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testCrossStageSmoothingKeepsBoth() {
		//Smoothing a spectrum and smoothing a map are different operations, so both are
		//reported. These used to collapse into a single "Smoothed".
		mc.rawDataController.getMapResultSet().setSourceTags(List.of(PLOT_SMOOTHED));
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals(List.of(PLOT_SMOOTHED, MAP_SMOOTHED), mc.getFiltering().getDataTags());
		assertEquals("Smoothed; Plot: Smoothed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testDisabledMapFilterContributesNoTag() {
		MapFilter filter = createMultiplyFilter();
		filter.setEnabled(false);
		mc.getFiltering().add(filter);
		assertNull(mc.getFiltering().getActionDescription());
	}

}
