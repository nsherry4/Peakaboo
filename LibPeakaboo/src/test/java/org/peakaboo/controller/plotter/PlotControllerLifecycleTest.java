package org.peakaboo.controller.plotter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class PlotControllerLifecycleTest {

	@BeforeClass
	public static void initRegistries() {
		CurveFitterRegistry.init();
		ChannelViewModeRegistry.init();
		FittingFunctionRegistry.init();
		FittingSolverRegistry.init();
		FilterRegistry.init();
	}

	private PlotController createController() {
		return new PlotController(null);
	}

	private void loadSyntheticData(PlotController controller, int numScans, int channels) {
		SyntheticDataSource ds = SyntheticDataSource.createWithFeKPeak(numScans, channels, 1000f);
		controller.data().setDataSource(ds, null, () -> false);
	}

	@Test
	public void testEmptyStateAfterConstruction() {
		PlotController pc = createController();
		assertFalse(pc.data().hasDataSet());
		assertNull(pc.currentScan());
		assertNull(pc.filtering().getFilteredPlot());
		assertFalse(pc.fitting().hasSelectionFitting());
	}

	@Test
	public void testSetDataSourceMakesDataAvailable() {
		PlotController pc = createController();
		loadSyntheticData(pc, 5, 2048);
		assertTrue(pc.data().hasDataSet());
		assertEquals(5, pc.data().getDataSet().getScanData().scanCount());
		assertEquals(2048, pc.data().getDataSet().getAnalysis().channelsPerScan());
	}

	@Test
	public void testSetDataSourceSetsEnergyCalibration() {
		PlotController pc = createController();
		loadSyntheticData(pc, 1, 2048);
		assertEquals(0f, pc.fitting().getMinEnergy(), 0.001f);
		assertEquals(20f, pc.fitting().getMaxEnergy(), 0.001f);
	}

	@Test
	public void testDataLoadFiresEvent() {
		PlotController pc = createController();
		List<PlotUpdateType> received = new ArrayList<>();
		pc.addListener(received::add);
		loadSyntheticData(pc, 1, 2048);
		assertTrue("Expected DATA event after load", received.contains(PlotUpdateType.DATA));
	}

	@Test
	public void testCurrentScanReturnsDataAfterLoad() {
		PlotController pc = createController();
		loadSyntheticData(pc, 3, 2048);
		SpectrumView scan = pc.currentScan();
		assertNotNull(scan);
		assertEquals(2048, scan.size());
	}

	@Test
	public void testSetDataSourceClearsUndoHistory() {
		PlotController pc = createController();
		loadSyntheticData(pc, 1, 2048);
		assertFalse(pc.history().canUndo());
	}

}
