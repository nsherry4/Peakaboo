package peakaboo.display.calibration;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class CalibrationProfilePlot extends ZCalibrationPlot {

	private CalibrationProfile profile;
	private File source;
	
	public CalibrationProfilePlot(CalibrationProfile profile, TransitionShell type, File source) {
		super(type);
		setData(profile, source);
	}

	public void setData(CalibrationProfile profile, File source) {
		this.profile = profile;
		this.source = source;
		super.configure();
	}
	
	@Override
	protected List<LegacyTransitionSeries> getKeys(TransitionShell type) {
		return profile.getTransitionSeries(type);
	}

	@Override
	protected Map<LegacyTransitionSeries, Float> getData() {
		Map<LegacyTransitionSeries, Float> cals = profile.getCalibrations();
		List<LegacyTransitionSeries> interpolated = profile.getInterpolated();
		
		Map<LegacyTransitionSeries, Float> data = new LinkedHashMap<>();
		for (LegacyTransitionSeries ts : cals.keySet()) {
			if (!interpolated.contains(ts)) {
				data.put(ts, cals.get(ts));
			}
		}
		return data;
	}
	
	@Override
	protected Map<LegacyTransitionSeries, Float> getFadedData() {
		Map<LegacyTransitionSeries, Float> cals = profile.getCalibrations();
		List<LegacyTransitionSeries> interpolated = profile.getInterpolated();
		
		Map<LegacyTransitionSeries, Float> faded = new LinkedHashMap<>();
		for (LegacyTransitionSeries ts : cals.keySet()) {
			if (interpolated.contains(ts)) {
				faded.put(ts, cals.get(ts));
			}
		}
		return faded;
	}

	@Override
	protected boolean isEmpty() {
		return profile.isEmpty();
	}

	@Override
	protected String getYAxisTitle() {
		String ylabel = "Sensitivity";
		if (!profile.isEmpty()) {
			ylabel = "Sensitivity versus " + profile.getReference().getAnchor().getElement().toString();
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
	
	@Override
	protected String getHighlightText(LegacyTransitionSeries ts) {
		if (profile.getInterpolated().contains(ts)) {
			return ts.getElement().toString() + " (Interpolated)";
		}
		return ts.getElement().toString();
	}
	
	@Override
	protected List<DataLabel> getLabels(int lowest, int highest) {
		if (getHighlighted() == null) {
			return Collections.emptyList();
		}
		DataLabel highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new LegacyTransitionSeries(getHighlighted(), getType())));
		return Collections.singletonList(highlightLabel);
	}



	
}
