package peakaboo.drawing.plot.painters.plot;


import java.awt.Color;
import java.util.List;



public class PrimaryPlotPainter extends AreaPainter
{

	public PrimaryPlotPainter(List<Double> data, boolean isMonochrome)
	{
		super(data, getTopColor(isMonochrome), getBottomColor(isMonochrome), getStrokeColor(isMonochrome));
	}
	public PrimaryPlotPainter(List<Double> data)
	{
		super(data, getTopColor(false), getBottomColor(false), getStrokeColor(false));
	}
	
	private static Color getTopColor(boolean isMonochrome)
	{
		if (isMonochrome) return new Color(0.5f, 0.5f, 0.5f);
		return new Color(0.508f, 0.722f, 0.314f);
	}
	
	private static Color getBottomColor(boolean isMonochrome)
	{
		if (isMonochrome) return new Color(0.35f, 0.35f, 0.35f);
		return new Color(0.306f, 0.604f, 0.024f);
	}
	
	private static Color getStrokeColor(boolean isMonochrome)
	{
		if (isMonochrome) return new Color(0.10f, 0.10f, 0.10f);;
		return new Color(0.204f, 0.404f, 0.016f);
	}
	

}
