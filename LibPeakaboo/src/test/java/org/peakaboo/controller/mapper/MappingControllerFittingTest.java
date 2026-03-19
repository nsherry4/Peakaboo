package org.peakaboo.controller.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.mapper.fitting.modes.CompositeModeController;
import org.peakaboo.controller.mapper.fitting.modes.CorrelationModeController;
import org.peakaboo.controller.mapper.fitting.modes.OverlayModeController;
import org.peakaboo.controller.mapper.fitting.modes.RatioModeController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SyntheticDataSource;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.correlation.CorrelationMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class MappingControllerFittingTest {

	private MappingController mc;
	private ITransitionSeries feK;
	private ITransitionSeries cuK;

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
		feK = SyntheticMapData.feK();
		cuK = SyntheticMapData.cuK();
	}

	// --- Composite mode (default) ---

	@Test
	public void testCompositeDataNotNull() {
		assertNotNull(mc.getFitting().getMapModeData());
	}

	@Test
	public void testCompositeDataHasCorrectSize() {
		MapModeData data = mc.getFitting().getMapModeData();
		assertEquals(5L, (long) data.getSize().x);
		assertEquals(5L, (long) data.getSize().y);
	}

	@Test
	public void testCompositeDataNonZero() {
		MapModeData data = mc.getFitting().getMapModeData();
		assertTrue("Map should have selection info", data.getMapSelectionInfo().isPresent());
		float max = data.getMapSelectionInfo().get().map().max();
		assertTrue("Composite data should have non-zero values", max > 0);
	}

	@Test
	public void testCompositeVisibilityToggle() {
		CompositeModeController composite = (CompositeModeController) mc.getFitting()
			.getModeController(CompositeMapMode.MODE_NAME).get();

		MapModeData bothVisible = composite.getData();
		float sumBoth = sumMapData(bothVisible);

		composite.setVisibility(feK, false);
		MapModeData cuOnly = composite.getData();
		float sumCuOnly = sumMapData(cuOnly);

		assertTrue("Hiding Fe K should change composite data", Math.abs(sumBoth - sumCuOnly) > 1f);
	}

	// --- Overlay mode ---

	@Test
	public void testOverlayModeSwitch() {
		mc.getFitting().setMapDisplayMode(OverlayMapMode.MODE_NAME);
		assertEquals(OverlayMapMode.MODE_NAME, mc.getFitting().getMapDisplayMode());
	}

	@Test
	public void testOverlayDataNotNull() {
		mc.getFitting().setMapDisplayMode(OverlayMapMode.MODE_NAME);
		assertNotNull(mc.getFitting().getMapModeData());
	}

	@Test
	public void testOverlayColourAssignment() {
		OverlayModeController overlay = (OverlayModeController) mc.getFitting()
			.getModeController(OverlayMapMode.MODE_NAME).get();
		overlay.setColour(feK, OverlayColour.RED);
		overlay.setColour(cuK, OverlayColour.GREEN);
		assertEquals(OverlayColour.RED, overlay.getColour(feK));
		assertEquals(OverlayColour.GREEN, overlay.getColour(cuK));
	}

	// --- Ratio mode ---

	@Test
	public void testRatioModeSwitch() {
		mc.getFitting().setMapDisplayMode(RatioMapMode.MODE_NAME);
		assertEquals(RatioMapMode.MODE_NAME, mc.getFitting().getMapDisplayMode());
	}

	@Test
	public void testRatioSideAssignment() {
		mc.getFitting().setMapDisplayMode(RatioMapMode.MODE_NAME);
		RatioModeController ratio = (RatioModeController) mc.getFitting()
			.getModeController(RatioMapMode.MODE_NAME).get();
		ratio.setSide(feK, 1);
		ratio.setSide(cuK, 2);
		assertNotNull(ratio.getData());
	}

	@Test
	public void testRatioDataHasValues() {
		mc.getFitting().setMapDisplayMode(RatioMapMode.MODE_NAME);
		RatioModeController ratio = (RatioModeController) mc.getFitting()
			.getModeController(RatioMapMode.MODE_NAME).get();
		ratio.setSide(feK, 1);
		ratio.setSide(cuK, 2);
		MapModeData data = ratio.getData();
		float sum = sumMapData(data);
		assertTrue("Ratio data should have some non-zero entries", sum != 0);
	}

	// --- Correlation mode ---

	@Test
	public void testCorrelationModeSwitch() {
		mc.getFitting().setMapDisplayMode(CorrelationMapMode.MODE_NAME);
		assertEquals(CorrelationMapMode.MODE_NAME, mc.getFitting().getMapDisplayMode());
	}

	@Test
	public void testCorrelationIsNotSpatial() {
		CorrelationModeController corr = (CorrelationModeController) mc.getFitting()
			.getModeController(CorrelationMapMode.MODE_NAME).get();
		assertFalse(corr.isSpatial());
	}

	@Test
	public void testCorrelationIsTranslatable() {
		CorrelationModeController corr = (CorrelationModeController) mc.getFitting()
			.getModeController(CorrelationMapMode.MODE_NAME).get();
		assertTrue(corr.isTranslatableToSpatial());
	}

	@Test
	public void testCorrelationSideAssignment() {
		mc.getFitting().setMapDisplayMode(CorrelationMapMode.MODE_NAME);
		CorrelationModeController corr = (CorrelationModeController) mc.getFitting()
			.getModeController(CorrelationMapMode.MODE_NAME).get();
		corr.setSide(feK, 1);
		corr.setSide(cuK, 2);
		MapModeData data = corr.getData();
		assertNotNull(data);
		int bins = corr.getBins();
		assertEquals((long) bins, (long) data.getSize().x);
		assertEquals((long) bins, (long) data.getSize().y);
	}

	// --- Scale mode ---

	@Test
	public void testScaleModeChange() {
		mc.getFitting().setMapScaleMode(MapScaleMode.RELATIVE);
		assertEquals(MapScaleMode.RELATIVE, mc.getFitting().getMapScaleMode());
	}

	@Test
	public void testScaleModeChangeFiresEvent() {
		List<MapUpdateType> received = new ArrayList<>();
		mc.addListener(received::add);
		mc.getFitting().setMapScaleMode(MapScaleMode.RELATIVE);
		assertTrue("Expected UI_OPTIONS event", received.contains(MapUpdateType.UI_OPTIONS));
	}

	private float sumMapData(MapModeData data) {
		if (data.getMapSelectionInfo().isEmpty()) {
			return 0f;
		}
		var spectrum = data.getMapSelectionInfo().get().map();
		float sum = 0;
		for (int i = 0; i < spectrum.size(); i++) {
			sum += spectrum.get(i);
		}
		return sum;
	}

}
