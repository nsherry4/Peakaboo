package peakaboo.ui.swing.calibration.concentration;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.calibration.Concentrations;
import peakaboo.display.calibration.ConcentrationPlot;

public class ConcentrationPlotPanel extends GraphicsPanel {

	private ConcentrationPlot plot;
	
	public ConcentrationPlotPanel(Concentrations comp) {
		plot = new ConcentrationPlot(comp);
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
