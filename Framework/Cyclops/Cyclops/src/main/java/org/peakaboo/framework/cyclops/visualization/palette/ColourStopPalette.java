package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.List;

public class ColourStopPalette extends ColourListPalette {

	
	public ColourStopPalette(PaletteColour colour) {
		this(Gradient.DEFAULT_STEPS, colour);
	}

	public ColourStopPalette(int steps, PaletteColour colour) {
		super(Gradient.extrapolateStops(steps, getMonochromeStops(colour)), false);
	}
	
	public ColourStopPalette(Gradient gradient) {
		this(Gradient.DEFAULT_STEPS, gradient, false);
	}

	public ColourStopPalette(Gradient gradient, boolean hasNegatives) {
		this(Gradient.DEFAULT_STEPS, gradient, hasNegatives);
	}
	
	public ColourStopPalette(int steps, Gradient gradient, boolean hasNegatives) {
		super(Gradient.extrapolateStops(steps, gradient), hasNegatives);
	}
	
	private static List<Gradient.Stop> getMonochromeStops(PaletteColour c) {
		return List.of(
			new Gradient.Stop(new PaletteColour(0xff000000), 0),
			new Gradient.Stop(c, 1)
		);
	}
	
}
