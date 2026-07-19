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
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;
import org.peakaboo.mapping.filter.model.MapFilter;
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

	private static float multiplierOf(MapFilter filter) {
		return (Float) filter.getParameters().get(0).getValue();
	}

	@Test
	public void testLoadedFilterIsDistinctInstance() {
		MultiplyMapFilter original = createMultiplyFilter(2f);
		mc.getFiltering().add(original);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertNotSame(original, fresh.getFiltering().get(0));
	}

	@Test
	public void testFilterEnabledStateIsIndependent() {
		//the original bug: two windows loaded from the same session shared one filter
		//instance, so toggling enabled in one toggled it in the other
		mc.getFiltering().add(createMultiplyFilter(2f));

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController first = freshController();
		MappingController second = freshController();
		session.loadInto(first);
		session.loadInto(second);

		first.getFiltering().setMapFilterEnabled(0, false);

		assertFalse(first.getFiltering().get(0).isEnabled());
		assertTrue(second.getFiltering().get(0).isEnabled());
		assertTrue(mc.getFiltering().get(0).isEnabled());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFilterParametersAreIndependent() {
		mc.getFiltering().add(createMultiplyFilter(2f));

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController first = freshController();
		MappingController second = freshController();
		session.loadInto(first);
		session.loadInto(second);

		((Parameter<Float>) first.getFiltering().get(0).getParameters().get(0)).setValue(5f);

		assertEquals(5f, multiplierOf(first.getFiltering().get(0)), 0f);
		assertEquals(2f, multiplierOf(second.getFiltering().get(0)), 0f);
		assertEquals(2f, multiplierOf(mc.getFiltering().get(0)), 0f);
	}

	@Test
	public void testDisabledFilterStaysDisabled() {
		mc.getFiltering().add(createMultiplyFilter(2f));
		mc.getFiltering().setMapFilterEnabled(0, false);

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController fresh = freshController();
		session.loadInto(fresh);

		assertFalse(fresh.getFiltering().get(0).isEnabled());
	}

	@Test
	public void testFilterSurvivesYamlRoundTrip() throws DruthersLoadException {
		mc.getFiltering().add(createMultiplyFilter(3f));
		mc.getFiltering().setMapFilterEnabled(0, false);

		String yaml = new SavedMapSession().storeFrom(mc).serialize();
		SavedMapSession loaded = DruthersSerializer.deserialize(yaml, false, SavedMapSession.class);
		MappingController fresh = freshController();
		loaded.loadInto(fresh);

		MapFilter filter = fresh.getFiltering().get(0);
		assertTrue(filter instanceof MultiplyMapFilter);
		assertFalse(filter.isEnabled());
		assertEquals(3f, multiplierOf(filter), 0f);
	}

	@Test
	public void testFilterSerializationUsesUuidNotClassName() {
		//filters are now keyed by plugin UUID (via SavedPlugin), not the implementation
		//class name, so moving/renaming a filter class doesn't break saved sessions
		mc.getFiltering().add(createMultiplyFilter(2f));

		String yaml = new SavedMapSession().storeFrom(mc).serialize();

		assertTrue(yaml.contains(new MultiplyMapFilter().pluginUUID()));
		assertFalse(yaml.contains(MultiplyMapFilter.class.getName()));
	}

	@Test
	public void testReserializeAfterDeserialize() throws DruthersLoadException {
		//re-serializing a deserialized session used to NPE because the filter getters
		//read through a live filter reference that deserialization never set
		mc.getFiltering().add(createMultiplyFilter(2f));

		String yaml = new SavedMapSession().storeFrom(mc).serialize();
		SavedMapSession loaded = DruthersSerializer.deserialize(yaml, false, SavedMapSession.class);
		String again = loaded.serialize();

		assertNotNull(again);
		assertFalse(again.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLoadedControllersShareNoState() {
		//guard against future too-shallow copies in any sub-session: load one session
		//into two controllers, mutate everything persisted in one, check the other
		mc.getUserDimensions().setUserDataWidth(5);
		mc.getUserDimensions().setUserDataHeight(5);
		mc.getSettings().setContours(true);
		mc.getSettings().setSpectrumSteps(8);
		mc.getFitting().setMapScaleMode(MapScaleMode.RELATIVE);
		mc.getFiltering().add(createMultiplyFilter(2f));

		SavedMapSession session = new SavedMapSession().storeFrom(mc);
		MappingController first = freshController();
		MappingController second = freshController();
		session.loadInto(first);
		session.loadInto(second);

		first.getUserDimensions().setUserDataWidth(3);
		first.getUserDimensions().setUserDataHeight(3);
		first.getSettings().setContours(false);
		first.getSettings().setSpectrumSteps(3);
		first.getFitting().setMapScaleMode(MapScaleMode.ABSOLUTE);
		first.getFiltering().setMapFilterEnabled(0, false);
		((Parameter<Float>) first.getFiltering().get(0).getParameters().get(0)).setValue(7f);

		assertEquals(5, second.getUserDimensions().getUserDataWidth());
		assertEquals(5, second.getUserDimensions().getUserDataHeight());
		assertTrue(second.getSettings().getContours());
		assertEquals(8, second.getSettings().getSpectrumSteps());
		assertEquals(MapScaleMode.RELATIVE, second.getFitting().getMapScaleMode());
		assertTrue(second.getFiltering().get(0).isEnabled());
		assertEquals(2f, multiplierOf(second.getFiltering().get(0)), 0f);
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
