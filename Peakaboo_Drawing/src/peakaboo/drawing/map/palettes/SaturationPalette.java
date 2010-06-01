package peakaboo.drawing.map.palettes;

import java.awt.Color;


public class SaturationPalette extends AbstractPalette
{

	private Color saturated, unsaturated;
	
	public SaturationPalette(Color saturated, Color unsaturated){
		
		this.saturated = saturated;
		this.unsaturated = unsaturated;
		
	}
	
	@Override
	public Color getFillColour(double intensity, double maximum)
	{
		if (intensity == maximum){
			return saturated;
		}
		return unsaturated;
	}

}
