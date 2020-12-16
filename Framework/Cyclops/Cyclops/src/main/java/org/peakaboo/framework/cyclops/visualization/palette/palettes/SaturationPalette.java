package org.peakaboo.framework.cyclops.visualization.palette.palettes;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class SaturationPalette extends AbstractPalette
{

	private PaletteColour saturated, unsaturated;
	
	public SaturationPalette(PaletteColour saturated, PaletteColour unsaturated){
		
		this.saturated = saturated;
		this.unsaturated = unsaturated;
		
	}
	
	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		if (intensity != 0f){
			return saturated;
		}
		return unsaturated;
	}

}
