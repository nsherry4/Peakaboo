package peakaboo.ui.swing.calibration.referenceplot;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.display.calibration.CalibrationReferencePlot;

public class ReferencePlot extends GraphicsPanel {

	CalibrationReferencePlot plot;
	
	public ReferencePlot(CalibrationReference reference, TransitionSeriesType type) {
		plot = new CalibrationReferencePlot(reference, type);
	}
	
	@Override
	protected void drawGraphics(Surface backend, Coord<Integer> size) {
		plot.draw(backend, size);
	}

	@Override
	public float getUsedWidth() {
		return getWidth();
	}

	@Override
	public float getUsedWidth(float zoom) {
		return getWidth();
	}

	@Override
	public float getUsedHeight() {
		return getHeight();
	}

	@Override
	public float getUsedHeight(float zoom) {
		return getHeight();
	}


	
}
