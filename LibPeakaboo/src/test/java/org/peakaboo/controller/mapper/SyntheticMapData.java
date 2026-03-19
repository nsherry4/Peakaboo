package org.peakaboo.controller.mapper;

import java.util.Collections;
import java.util.List;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.mapping.rawmap.RawMapSet;

/**
 * Factory for creating a {@link MappingController} backed by synthetic 2D map
 * data, for use in integration tests.
 */
public class SyntheticMapData {

	private static final KrausePeakTable TABLE = new KrausePeakTable();

	public static ITransitionSeries feK() {
		return TABLE.get(Element.Fe, TransitionShell.K);
	}

	public static ITransitionSeries cuK() {
		return TABLE.get(Element.Cu, TransitionShell.K);
	}

	/**
	 * Creates a {@link MappingController} with synthetic gradient data for Fe K
	 * and Cu K transition series.
	 *
	 * @param pc     a PlotController with data already loaded
	 * @param width  map width in pixels
	 * @param height map height in pixels
	 * @return a fully wired MappingController
	 */
	public static MappingController create(PlotController pc, int width, int height) {
		int mapSize = width * height;

		ITransitionSeries feK = feK();
		ITransitionSeries cuK = cuK();
		RawMapSet rawMapSet = new RawMapSet(List.of(feK, cuK), mapSize, true);

		// Fe K: left-to-right gradient
		for (int i = 0; i < mapSize; i++) {
			rawMapSet.putIntensityInMapAtPoint((i % width) * 10f, feK, i);
		}
		// Cu K: top-to-bottom gradient
		for (int i = 0; i < mapSize; i++) {
			rawMapSet.putIntensityInMapAtPoint((i / width) * 10f, cuK, i);
		}

		RawDataController rawDataController = new RawDataController();
		rawDataController.setMapData(
			rawMapSet,
			pc.data().getDataSet(),
			"Synthetic Map",
			Collections.emptyList(),
			new BasicDetectorProfile()
		);

		MappingController mc = new MappingController(rawDataController, pc);
		mc.getUserDimensions().setUserDataWidth(width);
		mc.getUserDimensions().setUserDataHeight(height);
		return mc;
	}

}
