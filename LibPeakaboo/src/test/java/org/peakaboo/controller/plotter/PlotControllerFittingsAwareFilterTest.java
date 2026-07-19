package org.peakaboo.controller.plotter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class PlotControllerFittingsAwareFilterTest {

	private PlotController pc;
	private ITransitionSeries feK;

	/**
	 * Adds the number of fitted transition series to every channel, making the
	 * filtered data observably dependent on the fitting model.
	 */
	private static class FittingCountingFilter extends AbstractFilter {

		private boolean useFittings;
		int invocations = 0;

		FittingCountingFilter(boolean useFittings) {
			this.useFittings = useFittings;
		}

		@Override
		public String getFilterName() {
			return "Fitting Counter";
		}

		@Override
		public String getFilterDescription() {
			return "Test filter whose output depends on the fitting list";
		}

		@Override
		public FilterDescriptor getFilterDescriptor() {
			return FilterDescriptor.MATHEMATICAL;
		}

		@Override
		public String getFilterUUID() {
			return "3af6f746-c9c1-4a95-bf01-1a5d5f4bd7e5";
		}

		@Override
		public void initialize() {}

		@Override
		public boolean canFilterSubset() {
			return true;
		}

		@Override
		public boolean pluginEnabled() {
			return true;
		}

		@Override
		public String pluginVersion() {
			return "1.0";
		}

		@Override
		public boolean usesFittings() {
			return useFittings;
		}

		@Override
		protected SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
			invocations++;
			int fittingCount = ctx
					.map(c -> c.fittings().getFittedTransitionSeries().size())
					.orElse(0);
			Spectrum result = new ArraySpectrum(data);
			for (int i = 0; i < result.size(); i++) {
				result.set(i, result.get(i) + fittingCount);
			}
			return result;
		}

	}

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
	}

	@Test
	public void testFittingChangeRerunsFittingsAwareFilter() {
		FittingCountingFilter filter = new FittingCountingFilter(true);
		filter.initialize();
		pc.filtering().addFilter(filter);

		SpectrumView before = pc.filtering().getFilteredPlot();
		pc.fitting().addTransitionSeries(feK);
		SpectrumView after = pc.filtering().getFilteredPlot();

		// The filter adds the fitting count to each channel, so the filtered data
		// only reflects the new fitting if the filter was re-run
		assertEquals(before.get(0) + 1f, after.get(0), 0.001f);
	}

	@Test
	public void testFittingChangeDoesNotRerunOrdinaryFilter() {
		FittingCountingFilter filter = new FittingCountingFilter(false);
		filter.initialize();
		pc.filtering().addFilter(filter);

		pc.filtering().getFilteredPlot();
		int invocationsBefore = filter.invocations;
		assertTrue(invocationsBefore > 0);

		pc.fitting().addTransitionSeries(feK);
		pc.filtering().getFilteredPlot();

		assertEquals(invocationsBefore, filter.invocations);
	}

}
