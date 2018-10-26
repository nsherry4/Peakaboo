package peakaboo.display.plot.painters;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import peakaboo.curvefit.curve.fitting.FittingResult;

public class FittingLabel {
	
	//passed in
	FittingResult fit;
	String annotation;
	PlotPalette palette;

	//derived by painters
	String title;
	Coord<Bounds<Float>> position;
	boolean viable = true;
	float penWidth = 1f;
	
	public FittingLabel(FittingResult fit, PlotPalette palette, String annotation) {
		this.fit = fit;
		this.annotation = annotation;
		this.palette = palette;
	}
	
	
}