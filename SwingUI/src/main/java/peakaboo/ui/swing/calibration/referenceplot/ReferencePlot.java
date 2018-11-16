package peakaboo.ui.swing.calibration.referenceplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;
import peakaboo.display.calibration.CalibrationReferencePlot;

public class ReferencePlot extends GraphicsPanel {

	CalibrationReferencePlot plot;
	
	public ReferencePlot(CalibrationReference reference, TransitionShell type) {
		plot = new CalibrationReferencePlot(reference, type);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				Element element = plot.getElement(plot.getIndex(e.getX()));
								
				boolean changed = plot.setHighlighted(element);
				if (changed) {
					ReferencePlot.this.repaint();
				}
			}

		});
		
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
