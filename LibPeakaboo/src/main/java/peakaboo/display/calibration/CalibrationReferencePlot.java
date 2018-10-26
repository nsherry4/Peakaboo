package peakaboo.display.calibration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CalibrationReferencePlot extends ZCalibrationPlot {

	private CalibrationReference ref;
	
	public CalibrationReferencePlot(CalibrationReference ref, TransitionSeriesType type) {
		super(type);
		this.ref = ref;
		super.initialize();
	}
	
	@Override
	protected List<TransitionSeries> getKeys(TransitionSeriesType type) {
		return ref.getTransitionSeries(type);
	}

	@Override
	protected Map<TransitionSeries, Float> getData() {
		return ref.getConcentrations();
	}

	@Override
	protected boolean isEmpty() {
		return ref.isEmpty();
	}

	@Override
	protected String getYAxisTitle() {
		return "Concentration";
	}

	@Override
	protected String getTitle() {
		return ref.getName();
	}

	@Override
	protected Function<Integer, String> getYAxisFormatter() {
		return i -> ""+i;
	}

}
