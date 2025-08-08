package org.peakaboo.framework.cyclops.visualization.palette;

public class SaturationPalette implements Palette
{

	private PaletteColour saturated, unsaturated;
	private int argbSaturated, argbUnsaturated;

	public SaturationPalette(PaletteColour saturated, PaletteColour unsaturated){
		
		this.saturated = saturated;
		this.unsaturated = unsaturated;

		this.argbSaturated = saturated.getARGB();
		this.argbUnsaturated = unsaturated.getARGB();
		
	}
	
	@Override
	public PaletteColour getFillColour(double intensity, double maximum)
	{
		if (intensity != 0f){
			return saturated;
		}
		return unsaturated;
	}

	@Override
	public int getARGBFillColour(double intensity, double maximum) {
		if (intensity != 0f){
			return argbSaturated;
		}
		return argbUnsaturated;
	}

}
