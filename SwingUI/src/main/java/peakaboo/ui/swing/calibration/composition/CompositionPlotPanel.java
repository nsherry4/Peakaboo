package peakaboo.ui.swing.calibration.composition;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.calibration.Composition;
import peakaboo.display.calibration.CompositionPlot;

public class CompositionPlotPanel extends GraphicsPanel {

	private CompositionPlot plot;
	
	public CompositionPlotPanel(Composition comp) {
		plot = new CompositionPlot(comp);
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
