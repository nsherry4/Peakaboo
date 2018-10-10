package peakaboo.display.plot.painters;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.visualization.palette.PaletteColour;
import peakaboo.curvefit.curve.FittingResult;

public class FittingLabel {
	
	public static class PlotPalette {
		public PaletteColour labelText;
		public PaletteColour labelBackground;
		public PaletteColour labelStroke;
		
		public PaletteColour fitFill;
		public PaletteColour fitStroke;
		public PaletteColour sumStroke;
		
		public PaletteColour markings;
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