package peakaboo.drawing.painters;

import java.util.List;

import peakaboo.datatypes.Coord;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.Drawing;
import peakaboo.drawing.DrawingRequest;

/**
 * 
 * This class is a structure for holding data needed by a {@link Painter} when drawing to a {@link Drawing}
 * 
 * @author Nathaniel Sherry, 2009
 * @see Painter
 * @see Drawing
 *
 */

public class PainterData
{
	public Surface context;
	public DrawingRequest dr;
	public Coord<Double> plotSize;
	public List<Double> dataHeights;
	
	
	public PainterData(Surface context, DrawingRequest dr, Coord<Double> plotSize, List<Double> dataHeights)
	{
		this.context = context;
		this.dr = dr;
		this.plotSize = plotSize;
		this.dataHeights = dataHeights;
	}
	
	public double getChannelXValue(double channel)
	{
		double channelSize = plotSize.x / dr.dataWidth;
		return channel * channelSize;
	}
	
}
