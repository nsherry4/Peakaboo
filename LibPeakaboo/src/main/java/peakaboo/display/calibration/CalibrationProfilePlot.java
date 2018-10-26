package peakaboo.display.calibration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CalibrationProfilePlot extends ZCalibrationPlot {

	private CalibrationProfile profile;
	private File source;
	
	public CalibrationProfilePlot(CalibrationProfile profile, TransitionSeriesType type, File source) {
		super(type);
		setData(profile, source);
	}

	public void setData(CalibrationProfile profile, File source) {
		this.profile = profile;
		this.source = source;
		super.initialize();
	}
	
	@Override
	protected List<TransitionSeries> getKeys(TransitionSeriesType type) {
		return profile.getTransitionSeries(type);
	}

	@Override
	protected Map<TransitionSeries, Float> getData() {
		return profile.getCalibrations();
	}

	@Override
	protected boolean isEmpty() {
		return profile.isEmpty();
	}

	@Override
	protected String getYAxisTitle() {
		String ylabel = "Sensitivity";
		if (!profile.isEmpty()) {
			ylabel = "Sensitivity versus " + profile.getReference().getAnchor().element.toString();
		}
		return ylabel;
	}

	@Override
	protected String getTitle() {
		String title = profile.getName();
		if (source != null) {
			title += " (" + source.getName() + ")";
		}
		return title;
	}

	@Override
	protected Function<Integer, String> getYAxisFormatter() {
		return i -> i + "%";
	}

	
}
