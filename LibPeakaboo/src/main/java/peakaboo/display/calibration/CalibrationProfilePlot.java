package peakaboo.display.calibration;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
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
		super.configure();
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
	
	@Override
	protected String getHighlightText(TransitionSeries ts) {
		return ts.element.toString();
	}
	
	@Override
	protected List<DataLabel> getLabels(int lowest, int highest) {
		if (getHighlighted() == null) {
			return Collections.emptyList();
		}
		DataLabel highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new TransitionSeries(getHighlighted(), getType())));
		return Collections.singletonList(highlightLabel);
	}

	
}
