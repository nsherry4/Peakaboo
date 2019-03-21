package cyclops.visualization.drawing.painters;


import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.visualization.Surface;
import cyclops.visualization.drawing.Drawing;
import cyclops.visualization.drawing.DrawingRequest;

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
	public Coord<Float> plotSize;
	public Spectrum dataHeights;
	public ReadOnlySpectrum originalHeights; //TODO: maybe dataHeights, decorationHeights?
	
	
	public PainterData(Surface context, DrawingRequest dr, Coord<Float> plotSize, Spectrum dataHeights)
	{
		this.context = context;
		this.dr = dr;
		this.plotSize = plotSize;
		this.dataHeights = dataHeights;
		this.originalHeights = new ISpectrum(dataHeights);
	}
	
	public double getChannelXValue(double channel)
	{
		double channelSize = plotSize.x / dr.dataWidth;
		return channel * channelSize;
	}
	
}
