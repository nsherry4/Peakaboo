package org.peakaboo.controller.plotter;

import static org.junit.Assert.*;

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
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class PlotControllerPipelineTest {

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

	@Test
	public void testFilteredPlotAvailableAfterDataLoad() {
		SpectrumView filtered = pc.filtering().getFilteredPlot();
		SpectrumView raw = pc.currentScan();
		assertNotNull(filtered);
		assertEquals(raw.size(), filtered.size());
		// With no filters, filtered should match raw
		for (int i = 0; i < raw.size(); i++) {
			assertEquals(raw.get(i), filtered.get(i), 0.001f);
		}
	}

	@Test
	public void testAddFilterChangesFilteredPlot() {
		SpectrumView original = pc.filtering().getFilteredPlot();
		pc.filtering().addFilter(createMultiplyFilter(2f));
		SpectrumView filtered = pc.filtering().getFilteredPlot();
		assertNotNull(filtered);
		// Check a channel with non-zero value
		for (int i = 0; i < original.size(); i++) {
			if (original.get(i) > 0) {
				assertEquals(original.get(i) * 2f, filtered.get(i), 0.01f);
			}
		}
	}

	@Test
	public void testRemoveFilterRestoresOriginal() {
		SpectrumView original = pc.filtering().getFilteredPlot();
		pc.filtering().addFilter(createMultiplyFilter(3f));
		pc.filtering().removeFilter(0);
		SpectrumView restored = pc.filtering().getFilteredPlot();
		for (int i = 0; i < original.size(); i++) {
			assertEquals(original.get(i), restored.get(i), 0.001f);
		}
	}

	@Test
	public void testFilterCacheInvalidatesOnFilterAdd() {
		SpectrumView before = pc.filtering().getFilteredPlot();
		pc.filtering().addFilter(createMultiplyFilter(2f));
		SpectrumView after = pc.filtering().getFilteredPlot();
		// After adding a filter the cache should return a different result
		assertNotSame(before, after);
	}

	@Test
	public void testAddTransitionSeriesProducesFittingResults() {
		pc.fitting().addTransitionSeries(feK);
		assertTrue(pc.fitting().hasSelectionFitting());
		assertEquals(1, pc.fitting().getFittedTransitionSeries().size());
	}

	@Test
	public void testFittingResultNonZeroForMatchingPeak() {
		pc.fitting().addTransitionSeries(feK);
		float intensity = pc.fitting().getTransitionSeriesIntensity(feK);
		assertTrue("Fe K intensity should be > 0 for matching peak", intensity > 0);
	}

	@Test
	public void testFittingResultLowForNonMatchingPeak() {
		pc.fitting().addTransitionSeries(feK);
		float feIntensity = pc.fitting().getTransitionSeriesIntensity(feK);

		pc.fitting().addTransitionSeries(cuK);
		float cuIntensity = pc.fitting().getTransitionSeriesIntensity(cuK);

		assertTrue("Cu K intensity should be less than Fe K", cuIntensity < feIntensity * 0.25f);
	}

	@Test
	public void testFilterChangeInvalidatesFittingResults() {
		pc.fitting().addTransitionSeries(feK);
		float intensityBefore = pc.fitting().getTransitionSeriesIntensity(feK);
		assertTrue(intensityBefore > 0);

		pc.filtering().addFilter(createMultiplyFilter(2f));
		float intensityAfter = pc.fitting().getTransitionSeriesIntensity(feK);

		// After doubling the data, the fitted intensity should roughly double
		assertEquals(intensityBefore * 2f, intensityAfter, intensityBefore * 0.5f);
	}

	@Test
	public void testFittingProposalsWorkflow() {
		pc.fitting().addTransitionSeries(feK);
		pc.fitting().addProposedTransitionSeries(cuK);
		assertTrue(pc.fitting().hasProposalFitting());
		assertEquals(1, pc.fitting().getProposedTransitionSeries().size());

		pc.fitting().commitProposedTransitionSeries();
		assertTrue(pc.fitting().getFittedTransitionSeries().contains(cuK));
		assertTrue(pc.fitting().getProposedTransitionSeries().isEmpty());
	}

}
