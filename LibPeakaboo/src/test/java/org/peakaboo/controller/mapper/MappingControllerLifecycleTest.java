package org.peakaboo.controller.mapper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SyntheticDataSource;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class MappingControllerLifecycleTest {

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

	@Test
	public void testSubControllersNotNull() {
		assertNotNull(mc.rawDataController);
		assertNotNull(mc.getUserDimensions());
		assertNotNull(mc.getFiltering());
		assertNotNull(mc.getFitting());
		assertNotNull(mc.getSettings());
		assertNotNull(mc.getSelection());
	}

	@Test
	public void testRawDataHasExpectedSize() {
		assertEquals(25, mc.rawDataController.getMapSize());
	}

	@Test
	public void testRawDataHasExpectedTransitionSeries() {
		assertEquals(2, mc.rawDataController.getMapResultSet().getAllTransitionSeries().size());
	}

	@Test
	public void testDefaultDisplayModeIsComposite() {
		assertEquals(CompositeMapMode.MODE_NAME, mc.getFitting().getMapDisplayMode());
	}

	@Test
	public void testDefaultScaleModeIsAbsolute() {
		assertEquals(MapScaleMode.ABSOLUTE, mc.getFitting().getMapScaleMode());
	}

	@Test
	public void testDefaultSettingsValues() {
		assertTrue(mc.getSettings().getShowCoords());
		assertTrue(mc.getSettings().getShowSpectrum());
		assertTrue(mc.getSettings().getShowTitle());
		assertFalse(mc.getSettings().getContours());
		assertFalse(mc.getSettings().getShowDatasetTitle());
	}

	@Test
	public void testNoSelectionByDefault() {
		assertFalse(mc.getSelection().hasSelection());
	}

	@Test
	public void testDatasetTitle() {
		assertEquals("Synthetic Map", mc.rawDataController.getDatasetTitle());
	}

}
