package org.peakaboo.framework.cyclops.visualization.drawing.painters;


import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.Drawing;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;

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
	public SpectrumView originalHeights; //TODO: maybe dataHeights, decorationHeights?
	
	
	public PainterData(Surface context, DrawingRequest dr, Coord<Float> plotSize, Spectrum dataHeights)
	{
		this.context = context;
		this.dr = dr;
		this.plotSize = plotSize;
		this.dataHeights = dataHeights;
		this.originalHeights = dataHeights == null ? null : new ArraySpectrum(dataHeights);
	}
	
	public double getChannelXValue(double channel)
	{
		double channelSize = plotSize.x / dr.dataWidth;
		return channel * channelSize;
	}
	
}
