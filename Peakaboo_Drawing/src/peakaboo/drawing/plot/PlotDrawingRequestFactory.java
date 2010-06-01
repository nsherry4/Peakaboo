package peakaboo.drawing.plot;

import peakaboo.drawing.DrawingRequest;


/**
 * @author Nathaniel Sherry, 2009
 * 
 *         This class is the DrawingRequest for plots. It extends the abstract DrawingRequest, adding
 *         parameters which are used just for plots
 * 
 */


// more like a struct than a class
public class PlotDrawingRequestFactory
{

	public static DrawingRequest getDrawingRequest()
	{
		// sensible defaults

		DrawingRequest dr = new DrawingRequest();
		
		dr.dataHeight = 1;
		dr.dataWidth = 2048;

		dr.imageHeight = 1;
		dr.imageWidth = 1;

		dr.drawToVectorSurface = true;

		dr.maxYIntensity = -1;

		dr.viewTransform = ViewTransform.LINEAR;
		dr.unitSize = 10.0;
		
		return dr;

	}

}
