package org.peakaboo.controller.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.mapper.dimensions.MapDimensionsController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SyntheticDataSource;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class MappingControllerDimensionsTest {

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
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(100, 2048, 1000f);
		pc.data().setDataSource(ds, null, () -> false);
		mc = SyntheticMapData.create(pc, 10, 10);
	}

	@Test
	public void testDefaultDimensions() {
		assertEquals(10, mc.getUserDimensions().getUserDataWidth());
		assertEquals(10, mc.getUserDimensions().getUserDataHeight());
	}

	@Test
	public void testSetWidthClamps() {
		mc.getUserDimensions().setUserDataWidth(20);
		// width * height cannot exceed mapSize (100), so height stays 10
		// and width gets clamped to 100/10 = 10
		assertTrue(mc.getUserDimensions().getUserDataWidth() * mc.getUserDimensions().getUserDataHeight() <= 100);
	}

	@Test
	public void testSetHeightClamps() {
		mc.getUserDimensions().setUserDataHeight(25);
		// width=10, 10*25=250 > 100, so height gets clamped to 100/10 = 10
		assertTrue(mc.getUserDimensions().getUserDataWidth() * mc.getUserDimensions().getUserDataHeight() <= 100);
	}

	@Test
	public void testWidthMinimumIsOne() {
		mc.getUserDimensions().setUserDataWidth(0);
		assertEquals(1, mc.getUserDimensions().getUserDataWidth());
	}

	@Test
	public void testHeightMinimumIsOne() {
		mc.getUserDimensions().setUserDataHeight(0);
		assertEquals(1, mc.getUserDimensions().getUserDataHeight());
	}

	@Test
	public void testDimensionChangeFiresEvent() {
		List<MapUpdateType> received = new ArrayList<>();
		mc.addListener(received::add);
		mc.getUserDimensions().setUserDataWidth(5);
		assertTrue("Expected DATA_SIZE event", received.contains(MapUpdateType.DATA_SIZE));
	}

	@Test
	public void testFilteredDimensionsMatchUserDimensions() {
		assertEquals(mc.getUserDimensions().getUserDataWidth(), mc.getFiltering().getFilteredDataWidth());
		assertEquals(mc.getUserDimensions().getUserDataHeight(), mc.getFiltering().getFilteredDataHeight());
	}

	@Test
	public void testValidPointWithinBounds() {
		assertTrue(mc.getUserDimensions().isValidPoint(new Coord<>(5, 5)));
	}

	@Test
	public void testInvalidPointOutOfBounds() {
		assertFalse(mc.getUserDimensions().isValidPoint(new Coord<>(10, 10)));
	}

}
