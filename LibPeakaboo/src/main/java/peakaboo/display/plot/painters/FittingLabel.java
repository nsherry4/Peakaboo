package peakaboo.display.plot.painters;

import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.visualization.palette.PaletteColour;

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