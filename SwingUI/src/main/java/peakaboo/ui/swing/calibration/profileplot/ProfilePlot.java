package peakaboo.ui.swing.calibration.profileplot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Spectrum;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.ViewTransform;
import cyclops.visualization.drawing.painters.axis.AxisPainter;
import cyclops.visualization.drawing.painters.axis.LineAxisPainter;
import cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import cyclops.visualization.drawing.plot.PlotDrawing;
import cyclops.visualization.drawing.plot.painters.PlotPainter;
import cyclops.visualization.drawing.plot.painters.PlotPainter.TraceType;
import cyclops.visualization.drawing.plot.painters.axis.GridlinePainter;
import cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter.TickFormatter;
import cyclops.visualization.drawing.plot.painters.plot.AreaPainter;
import cyclops.visualization.palette.PaletteColour;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.display.calibration.CalibrationProfilePlot;

public class ProfilePlot extends GraphicsPanel {

	private CalibrationProfilePlot plot;

	public ProfilePlot(CalibrationProfile profile, File source, TransitionSeriesType type) {
		plot = new CalibrationProfilePlot(profile, type, source);
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
