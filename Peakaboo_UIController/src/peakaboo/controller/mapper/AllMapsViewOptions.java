package peakaboo.controller.mapper;

import scitypes.Coord;



public class AllMapsViewOptions
{

	public boolean drawCoordinates = true;
	public Coord<Number> topRightCoord, topLeftCoord, bottomRightCoord, bottomLeftCoord;

	public boolean drawSpectrum = true;
	public boolean drawTitle = true;
	public boolean showDataSetTitle = false;;
	
	public int spectrumSteps = 15;
	public boolean contour = false;
	public int interpolation = 0;
	public boolean monochrome = false;
	public int spectrumHeight = 15;
	
	public boolean yflip = true;
	
}
