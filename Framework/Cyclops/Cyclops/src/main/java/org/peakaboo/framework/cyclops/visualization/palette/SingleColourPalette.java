package org.peakaboo.framework.cyclops.visualization.palette;

public class SingleColourPalette implements Palette
{

	private PaletteColour colour;
	
	public SingleColourPalette(PaletteColour c)
	{
		colour = c;
	}
	
	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		return colour;
	}

}
