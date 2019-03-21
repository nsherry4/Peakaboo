package cyclops.visualization.palette.palettes;

import cyclops.visualization.palette.PaletteColour;

public class SingleColourPalette extends AbstractPalette
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
