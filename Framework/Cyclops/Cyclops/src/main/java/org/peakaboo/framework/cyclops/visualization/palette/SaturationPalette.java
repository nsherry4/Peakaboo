package org.peakaboo.framework.cyclops.visualization.palette;

public class SaturationPalette implements Palette
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
