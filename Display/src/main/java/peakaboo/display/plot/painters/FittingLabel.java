package peakaboo.display.plot.painters;

import java.awt.Color;

import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.Bounds;
import scitypes.Coord;

public class FittingLabel {
	
	public static class PlotPalette {
		public Color labelText;
		public Color labelBackground;
		public Color labelStroke;
		
		public Color fitFill;
		public Color fitStroke;
		public Color sumStroke;
		
		public Color markings;
	}
	
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