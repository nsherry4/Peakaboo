package peakaboo.controller.mapper.mapset;


import java.util.ArrayList;
import java.util.List;

import peakaboo.mapping.results.MapResultSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;



public class MapSetMapData
{

	
	//Map data
	public Coord<Bounds<Number>>	realDimensions;
	public SISize					realDimensionsUnits;
	
	public Coord<Integer>			dataDimensions		= new Coord<Integer>(1, 1);
	public Coord<Integer>			originalDimensions	= new Coord<Integer>(1, 1);
	public boolean					dimensionsProvided	= false;

	public MapResultSet				mapResults			= null;
	public String					datasetTitle		= "";
	public Coord<Number>			topRightCoord, topLeftCoord, bottomRightCoord, bottomLeftCoord;
	
	public List<Integer>			badPoints			= new ArrayList<Integer>();
	

	public int mapSize()
	{
		return mapResults.size();
	}
	
}
