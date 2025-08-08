package org.peakaboo.framework.cyclops.visualization.palette;

public class SingleColourPalette implements Palette
{

	private PaletteColour colour;
	private int argb;
	
	public SingleColourPalette(PaletteColour c)
	{
		colour = c;
		argb = c.getARGB();
	}
	
	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		return colour;
	}

	@Override
	public int getARGBFillColour(double intensity, double maximum) {
		return argb;
	}

}
