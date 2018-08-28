package peakaboo.display.plot.painters;

import java.awt.Color;

import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.Bounds;
import scitypes.Coord;

public class FittingLabel {
	
	//passed in
	FittingResult fit;
	String annotation;
	Color colour;

	//derived by painters
	String title;
	Coord<Bounds<Float>> position;
	boolean viable = true;
	float penWidth = 1f;
	
	public FittingLabel(FittingResult fit, Color colour, String annotation) {
		this.fit = fit;
		this.annotation = annotation;
		this.colour = colour;
	}
	
	
}