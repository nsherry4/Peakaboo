package peakaboo.drawing.map.palettes;

import java.awt.Color;


public class SingleColourPalette extends AbstractPalette
{

	private Color colour;
	
	public SingleColourPalette(Color c)
	{
		colour = c;
	}
	
	@Override
	public Color getFillColour(double intensity, double maximum)
	{
		return colour;
	}

}
