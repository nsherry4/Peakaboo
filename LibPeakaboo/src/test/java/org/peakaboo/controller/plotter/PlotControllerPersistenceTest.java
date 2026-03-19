package org.peakaboo.controller.plotter;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;

public class PlotControllerPersistenceTest {

	private PlotController pc;
	private ITransitionSeries feK;
	private ITransitionSeries cuK;

	@BeforeClass
	public static void initRegistries() {
		CurveFitterRegistry.init();
		ChannelViewModeRegistry.init();
		FittingFunctionRegistry.init();
		FittingSolverRegistry.init();
		FilterRegistry.init();
	}

	@Before
	public void setUp() {
		pc = new PlotController(null);
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(5, 2048, 1000f);
		pc.data().setDataSource(ds, null, () -> false);
		pc.data().setDataPaths(DataInputAdapter.fromFilenames(List.of("/tmp/dummy.dat")));

		KrausePeakTable table = new KrausePeakTable();
		feK = table.get(Element.Fe, TransitionShell.K);
		cuK = table.get(Element.Cu, TransitionShell.K);
	}

	@SuppressWarnings("unchecked")
	private MultiplicationMathFilter createMultiplyFilter(float multiplier) {
		MultiplicationMathFilter filter = new MultiplicationMathFilter();
		filter.initialize();
		((Value<Float>) filter.getParameters().get(0)).setValue(multiplier);
		return filter;
	}

	private PlotController createFreshControllerWithSameData() {
		PlotController fresh = new PlotController(null);
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(5, 2048, 1000f);
		fresh.data().setDataSource(ds, null, () -> false);
		fresh.data().setDataPaths(DataInputAdapter.fromFilenames(List.of("/tmp/dummy.dat")));
		return fresh;
	}

	@Test
	public void testSaveProducesNonEmptyYaml() {
		String yaml = pc.save().serialize();
		assertNotNull(yaml);
		assertFalse(yaml.isEmpty());
	}

	@Test
	public void testSaveLoadRoundTripPreservesFittings() throws DruthersLoadException {
		pc.fitting().addTransitionSeries(feK);
		pc.fitting().addTransitionSeries(cuK);
		String yaml = pc.save().serialize();

		PlotController fresh = createFreshControllerWithSameData();
		fresh.load(yaml, false);

		List<ITransitionSeries> loaded = fresh.fitting().getFittedTransitionSeries();
		assertEquals(2, loaded.size());
	}

	@Test
	public void testSaveLoadRoundTripPreservesViewSettings() throws DruthersLoadException {
		pc.view().setViewLog(true);
		pc.view().setZoom(2.5f);
		String yaml = pc.save().serialize();

		PlotController fresh = createFreshControllerWithSameData();
		fresh.load(yaml, false);

		assertTrue(fresh.view().getViewLog());
		assertEquals(2.5f, fresh.view().getZoom(), 0.001f);
	}

	@Test
	public void testSaveLoadRoundTripPreservesFilters() throws DruthersLoadException {
		pc.filtering().addFilter(createMultiplyFilter(3f));
		String yaml = pc.save().serialize();

		PlotController fresh = createFreshControllerWithSameData();
		fresh.load(yaml, false);

		assertEquals(1, fresh.filtering().getFilterCount());
	}

	@Test
	public void testUndoRestoresPreviousState() {
		pc.fitting().addTransitionSeries(feK);
		pc.fitting().addTransitionSeries(cuK);

		assertTrue(pc.history().canUndo());
		pc.history().undo();

		List<ITransitionSeries> after = pc.fitting().getFittedTransitionSeries();
		assertEquals(1, after.size());
		assertTrue(after.contains(feK));
	}

	@Test
	public void testRedoRestoresUndoneState() {
		pc.fitting().addTransitionSeries(feK);
		pc.fitting().addTransitionSeries(cuK);

		pc.history().undo();
		assertTrue(pc.history().canRedo());
		pc.history().redo();

		List<ITransitionSeries> after = pc.fitting().getFittedTransitionSeries();
		assertEquals(2, after.size());
	}

	@Test
	public void testUndoFilterChange() {
		pc.filtering().addFilter(createMultiplyFilter(2f));
		assertEquals(1, pc.filtering().getFilterCount());

		assertTrue(pc.history().canUndo());
		pc.history().undo();
		assertEquals(0, pc.filtering().getFilterCount());
	}

}
