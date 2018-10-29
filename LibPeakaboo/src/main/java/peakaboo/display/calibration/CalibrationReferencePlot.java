package peakaboo.display.calibration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CalibrationReferencePlot extends ZCalibrationPlot {

	private CalibrationReference ref;
	
	public CalibrationReferencePlot(CalibrationReference ref, TransitionSeriesType type) {
		super(type);
		this.ref = ref;
		super.configure();
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
		DataLabel highlightLabel = null;
		if (getHighlighted() != null) {
			highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new TransitionSeries(getHighlighted(), getType())));
		}
		
		List<DataLabel> labels = new ArrayList<>();
		boolean addedHighlight = false;
		
		for (TransitionSeries ts : ref.getTransitionSeries(getType())) {

			String annotation = ref.getAnnotation(ts);
			if (annotation.trim().length() == 0) {
				continue;
			}
			
			if (ts.element == getHighlighted() && highlightLabel != null) {
				labels.add(highlightLabel);
				addedHighlight = true;
				continue;
			} else {
				DataLabel label = new DataLabel(PlotPalette.blackOnWhite(), ts.element.ordinal() - lowest, getHighlightText(new TransitionSeries(ts.element, getType())));
				labels.add(label);				
			}
			
			

			
		}
		
		if (!addedHighlight && highlightLabel != null) {
			labels.add(highlightLabel);
		}
		
		return labels;
		
	}

}
