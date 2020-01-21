package org.peakaboo.display.calibration;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;

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
	protected List<ITransitionSeries> getKeys(TransitionShell type) {
		return profile.getTransitionSeries(type);
	}

	@Override
	protected Map<ITransitionSeries, Float> getData() {
		Map<ITransitionSeries, Float> cals = profile.getCalibrations();
		List<ITransitionSeries> interpolated = profile.getInterpolated();
		
		Map<ITransitionSeries, Float> data = new LinkedHashMap<>();
		for (ITransitionSeries ts : cals.keySet()) {
			if (!interpolated.contains(ts)) {
				data.put(ts, cals.get(ts));
			}
		}
		return data;
	}
	
	@Override
	protected Map<ITransitionSeries, Float> getFadedData() {
		Map<ITransitionSeries, Float> cals = profile.getCalibrations();
		List<ITransitionSeries> interpolated = profile.getInterpolated();
		
		Map<ITransitionSeries, Float> faded = new LinkedHashMap<>();
		for (ITransitionSeries ts : cals.keySet()) {
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
	protected String getHighlightText(ITransitionSeries ts) {
		String title = ts.getElement().toString() + " " + Math.round(profile.getCalibration(ts)*100f) + "%";
		if (profile.getInterpolated().contains(ts)) {
			return title + " (Interpolated)";
		}
		return title;
	}
	
	@Override
	protected List<DataLabel> getLabels(int lowest, int highest) {
		if (getHighlighted() == null) {
			return Collections.emptyList();
		}
		DataLabel highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new PrimaryTransitionSeries(getHighlighted(), getType())));
		return Collections.singletonList(highlightLabel);
	}



	
}
