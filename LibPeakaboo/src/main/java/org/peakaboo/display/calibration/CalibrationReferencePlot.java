package org.peakaboo.display.calibration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;

public class CalibrationReferencePlot extends ZCalibrationPlot {

	private CalibrationReference ref;
	
	public CalibrationReferencePlot(CalibrationReference ref, TransitionShell type) {
		super(type);
		this.ref = ref;
		super.configure();
	}
	
	@Override
	protected List<ITransitionSeries> getKeys(TransitionShell type) {
		return ref.getTransitionSeries(type);
	}

	@Override
	protected Map<ITransitionSeries, Float> getData() {
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
	protected String getHighlightText(ITransitionSeries ts) {
		String title = ts.getElement().toString();
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
		DataLabel highlightLabel = new DataLabel(PlotPalette.blackOnWhite(), getHighlighted().ordinal() - lowest, getHighlightText(new PrimaryTransitionSeries(getHighlighted(), getType())));
		return Collections.singletonList(highlightLabel);
	}

	@Override
	protected Map<ITransitionSeries, Float> getFadedData() {
		return new HashMap<>();
	}
}
