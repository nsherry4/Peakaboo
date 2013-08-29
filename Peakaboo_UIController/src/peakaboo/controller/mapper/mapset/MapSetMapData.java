package peakaboo.controller.mapper.mapset;


import peakaboo.mapping.results.MapResultSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import fava.functionable.FList;



public class MapSetMapData
{

	
	//Map data
	public Coord<Bounds<Number>>	realDimensions;
	public SISize					realDimensionsUnits;
	public Coord<Integer>			dataDimensions		= new Coord<Integer>(1, 1);
	public boolean					dimensionsProvided	= false;

	public MapResultSet				mapResults			= null;
	public String					datasetTitle		= "";
	public Coord<Number>			topRightCoord, topLeftCoord, bottomRightCoord, bottomLeftCoord;
	
	public FList<Integer>			badPoints			= new FList<Integer>();
	

	public int mapSize()
	{
		return mapResults.size();
	}
	
}
