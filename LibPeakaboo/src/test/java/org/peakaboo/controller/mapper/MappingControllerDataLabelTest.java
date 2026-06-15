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
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.FastAverageMapFilter;

public class MappingControllerDataLabelTest {

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
	public void testNoLabelsInitially() {
		assertTrue(mc.getFiltering().getDataLabels().isEmpty());
		assertNull(mc.getFiltering().getActionDescription());
	}

	@Test
	public void testMapFilterLabels() {
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals(List.of(DataLabel.SMOOTHED), mc.getFiltering().getDataLabels());
		assertEquals("Smoothed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testInheritedSourceLabels() {
		mc.rawDataController.getMapResultSet().setSourceLabels(List.of(DataLabel.BACKGROUND_REMOVED));
		assertEquals(List.of(DataLabel.BACKGROUND_REMOVED), mc.getFiltering().getDataLabels());
		assertEquals("Background Removed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testInheritedAndMapFilterLabelsCombine() {
		mc.rawDataController.getMapResultSet().setSourceLabels(List.of(DataLabel.BACKGROUND_REMOVED));
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals("Background Removed, Smoothed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testCrossStageDedup() {
		//Smoothed at the plot stage, then smoothed again at the map stage: one label
		mc.rawDataController.getMapResultSet().setSourceLabels(List.of(DataLabel.SMOOTHED));
		mc.getFiltering().add(createSmoothingFilter());
		assertEquals("Smoothed", mc.getFiltering().getActionDescription());
	}

	@Test
	public void testDisabledMapFilterContributesNoLabel() {
		MapFilter filter = createMultiplyFilter();
		filter.setEnabled(false);
		mc.getFiltering().add(filter);
		assertNull(mc.getFiltering().getActionDescription());
	}

}
