package org.peakaboo.controller.plotter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.peakaboo.app.Settings;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.datalabel.DataLabel;
import org.peakaboo.datalabel.DataScope;
import org.peakaboo.datalabel.DataTag;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.filter.plugins.mathematical.MultiplicationMathFilter;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.mapping.rawmap.RawMapSet;

public class PlotControllerDataTagTest {

	private static final DataTag PLOT_OTHER = new DataTag(DataScope.PLOT, DataLabel.OTHER_FILTERING);

	private PlotController pc;

	@BeforeClass
	public static void initRegistries() throws IOException {
		Settings.load(Files.createTempDirectory("peakaboo-test-settings").toFile());
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
	}

	private MultiplicationMathFilter createMultiplyFilter() {
		MultiplicationMathFilter filter = new MultiplicationMathFilter();
		filter.initialize();
		return filter;
	}

	@Test
	public void testNoTagsInitially() {
		assertTrue(pc.getDataTags().isEmpty());
		assertTrue(pc.getPlotData().dataTags.isEmpty());
	}

	@Test
	public void testFilterTagsAppearInPlotData() {
		pc.filtering().addFilter(createMultiplyFilter());
		assertEquals(List.of(PLOT_OTHER), pc.getDataTags());
		assertEquals(List.of(PLOT_OTHER), pc.getPlotData().dataTags);
	}

	@Test
	public void testMapTaskSnapshotsSourceTags() {
		pc.filtering().addFilter(createMultiplyFilter());
		KrausePeakTable table = new KrausePeakTable();
		pc.fitting().addTransitionSeries(table.get(Element.Fe, TransitionShell.K));

		StreamExecutor<RawMapSet> task = pc.getMapTask();
		Optional<RawMapSet> result = task.run();
		assertTrue(result.isPresent());
		assertEquals(List.of(PLOT_OTHER), result.get().getSourceTags());

		//The snapshot must not change when the plot's filters change afterwards
		pc.filtering().removeFilter(0);
		assertTrue(pc.getDataTags().isEmpty());
		assertEquals(List.of(PLOT_OTHER), result.get().getSourceTags());
	}

}
