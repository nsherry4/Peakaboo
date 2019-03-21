package cyclops.visualization.drawing.plot.painters.plot;


import cyclops.ReadOnlySpectrum;
import cyclops.visualization.palette.PaletteColour;




public class PrimaryPlotPainter extends AreaPainter
{

	public PrimaryPlotPainter(ReadOnlySpectrum data, boolean isMonochrome)
	{
		super(data, getTopColor(isMonochrome), getBottomColor(isMonochrome), getStrokeColor(isMonochrome));
	}
	public PrimaryPlotPainter(ReadOnlySpectrum data)
	{
		super(data, getTopColor(false), getBottomColor(false), getStrokeColor(false));
	}
	
	private static PaletteColour getTopColor(boolean isMonochrome)
	{
		if (isMonochrome) return new PaletteColour(0xff606060);
		return new PaletteColour(0xff388E3C); //material green 700
	}
	
	private static PaletteColour getBottomColor(boolean isMonochrome)
	{
		if (isMonochrome) return new PaletteColour(0xff707070);
		return new PaletteColour(0xff43A047); //material green 600
	}
	
	private static PaletteColour getStrokeColor(boolean isMonochrome)
	{
		if (isMonochrome) return new PaletteColour(0xff202020);
		return new PaletteColour(0xff1B5E20); //material green 900
	}
	

}
