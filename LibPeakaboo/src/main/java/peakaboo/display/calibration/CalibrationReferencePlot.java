package peakaboo.display.calibration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class CalibrationReferencePlot extends ZCalibrationPlot {

	private CalibrationReference ref;
	
	public CalibrationReferencePlot(CalibrationReference ref, TransitionShell type) {
		super(type);
		this.ref = ref;
		super.configure();
	}
	
	@Override
	protected List<TransitionSeries> getKeys(TransitionShell type) {
		return ref.getTransitionSeries(type);
	}

	@Override
	protected Map<TransitionSeries, Float> getData() {
		return ref.getConcentrations();
	}

	@Override
	protected boolean isEmpty() {
		return ref.hasConcentrations();
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

	@Override
	protected String getHighlightText(TransitionSeries ts) {
		String title = ts.element.toString();
		String annotation = ref.getAnnotation(ts);
		if (annotation.trim().length() > 0) {
			title += ": " + annotation;
		}
		return title;
	}

	@Override
	protected List<DataLabel> getLabels(int lowest, int highest) {
		if (getHighlighted() == null) {
			return Collections.emptyList();
		}
		DataLabel highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new TransitionSeries(getHighlighted(), getType())));
		return Collections.singletonList(highlightLabel);
	}

	@Override
	protected Map<TransitionSeries, Float> getFadedData() {
		return new HashMap<>();
	}
}
