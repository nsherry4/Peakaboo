package peakaboo.controller.mapper.data;


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
	
	
	public Coord<Integer>			originalDimensions	= new Coord<Integer>(1, 1);
	public boolean					originalDimensionsProvided = false;
	
	public MapResultSet				mapResults			= null;
	public String					datasetTitle		= "";
	
	public List<Integer>			badPoints			= new ArrayList<Integer>();
	

	public int mapSize()
	{
		return mapResults.size();
	}
	
}
