package org.peakaboo.controller.mapper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SyntheticDataSource;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;

public class MappingControllerPersistenceTest {

	private PlotController pc;
	private RawDataController rawDataController;
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
		pc = new PlotController(null);
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(25, 2048, 1000f);
		pc.data().setDataSource(ds, null, () -> false);
		mc = SyntheticMapData.create(pc, 5, 5);
		rawDataController = mc.rawDataController;
	}

	private MappingController freshController() {
		return new MappingController(rawDataController, pc);
	}

	@SuppressWarnings("unchecked")
	private MultiplyMapFilter createMultiplyFilter(float multiplier) {
		MultiplyMapFilter filter = new MultiplyMapFilter();
		filter.initialize();
		((Parameter<Float>) filter.getParameters().get(0)).setValue(multiplier);
		return filter;
	}

	@Test
	public void testStoreAndLoadDimensions() {
		mc.getUserDimensions().setUserDataWidth(5);
		mc.getUserDimensions().setUserDataHeight(5);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertEquals(5, fresh.getUserDimensions().getUserDataWidth());
		assertEquals(5, fresh.getUserDimensions().getUserDataHeight());
	}

	@Test
	public void testStoreAndLoadSettings() {
		mc.getSettings().setContours(true);
		mc.getSettings().setSpectrumSteps(10);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertTrue(fresh.getSettings().getContours());
		assertEquals(10, fresh.getSettings().getSpectrumSteps());
	}

	@Test
	public void testStoreAndLoadScaleMode() {
		mc.getFitting().setMapScaleMode(MapScaleMode.RELATIVE);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertEquals(MapScaleMode.RELATIVE, fresh.getFitting().getMapScaleMode());
	}

	@Test
	public void testStoreAndLoadFilters() {
		mc.getFiltering().add(createMultiplyFilter(2f));

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertEquals(1, fresh.getFiltering().size());
	}

	@Test
	public void testFullRoundTrip() {
		mc.getUserDimensions().setUserDataWidth(5);
		mc.getUserDimensions().setUserDataHeight(5);
		mc.getSettings().setContours(true);
		mc.getSettings().setSpectrumSteps(8);
		mc.getFitting().setMapScaleMode(MapScaleMode.RELATIVE);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertEquals(5, fresh.getUserDimensions().getUserDataWidth());
		assertEquals(5, fresh.getUserDimensions().getUserDataHeight());
		assertTrue(fresh.getSettings().getContours());
		assertEquals(8, fresh.getSettings().getSpectrumSteps());
		assertEquals(MapScaleMode.RELATIVE, fresh.getFitting().getMapScaleMode());
	}

	@Test
	public void testSerializationProducesNonEmptyString() {
		String serialized = new SavedMapSession().storeFrom(mc).serialize();
		assertNotNull(serialized);
		assertFalse(serialized.isEmpty());
	}

	@Test
	public void testCSVExport() {
		String csv = mc.getCSV();
		assertNotNull(csv);
		assertFalse(csv.isEmpty());
	}

}
