package org.peakaboo.ui.swing.calibration.concentration;

import org.peakaboo.calibration.Concentrations;
import org.peakaboo.display.calibration.ConcentrationPlot;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.GraphicsPanel;

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
