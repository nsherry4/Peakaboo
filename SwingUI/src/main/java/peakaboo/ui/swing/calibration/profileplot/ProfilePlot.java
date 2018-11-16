package peakaboo.ui.swing.calibration.profileplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionShell;
import peakaboo.display.calibration.CalibrationProfilePlot;

public class ProfilePlot extends GraphicsPanel {

	private CalibrationProfilePlot plot;

	public ProfilePlot(CalibrationProfile profile, File source, TransitionShell type) {
		plot = new CalibrationProfilePlot(profile, type, source);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				Element element = plot.getElement(plot.getIndex(e.getX()));
								
				boolean changed = plot.setHighlighted(element);
				if (changed) {
					ProfilePlot.this.repaint();
				}
			}

		});
		
	}
	
	public void setCalibrationProfile(CalibrationProfile profile, File source) {
		plot.setData(profile, source);
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
