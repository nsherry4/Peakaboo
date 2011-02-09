package peakaboo.controller.mapper.maps;



import java.util.ArrayList;
import java.util.List;

import fava.datatypes.Bounds;

import peakaboo.mapping.results.MapResultSet;
import scitypes.Coord;
import scitypes.SISize;



public class AllMapsModel
{

	public List<Integer>			badPoints			= new ArrayList<Integer>();

	public Coord<Bounds<Number>>	realDimensions;
	public SISize					realDimensionsUnits;
	public Coord<Integer>			dataDimensions		= new Coord<Integer>(1, 1);
	public boolean					dimensionsProvided	= false;


	public boolean					drawCoordinates		= true;
	public Coord<Number>			topRightCoord, topLeftCoord, bottomRightCoord, bottomLeftCoord;

	public boolean					drawSpectrum		= true;
	public boolean					drawTitle			= true;
	public boolean					showDataSetTitle	= false;

	public int						spectrumSteps		= 15;
	public boolean					contour				= false;
	public int						interpolation		= 0;
	public boolean					monochrome			= false;

	public MapResultSet				mapResults			= null;

	public String					datasetTitle		= "";


	public int mapSize()
	{
		return mapResults.size();
	}

}
