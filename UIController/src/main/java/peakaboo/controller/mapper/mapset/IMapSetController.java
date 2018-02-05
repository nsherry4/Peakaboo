package peakaboo.controller.mapper.mapset;



import java.util.List;

import peakaboo.mapping.results.MapResultSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;



public interface IMapSetController
{

	void setMapData(
			MapResultSet data,
			String datasetName,
			Coord<Integer> dataDimensions,
			Coord<Bounds<Number>> realDimensions,
			SISize realDimensionsUnits,
			List<Integer> badPoints
	);
	void setMapData(
			MapResultSet data,
			String datasetName,
			List<Integer> badPoints
	);
	
	MapResultSet getMapResultSet();
	int getMapSize();

	
	Coord<Integer> guessDataDimensions();
	
	//only return non-zero if isDimensionsProvided is true
	int getOriginalDataHeight();
	int getOriginalDataWidth();
	
	String getDatasetTitle();
	void setDatasetTitle(String name);
	
	List<Integer> getBadPoints();
	
	
	boolean isValidPoint(Coord<Integer> mapCoord);

	
	Coord<Number> getTopLeftCoord();
	Coord<Number> getTopRightCoord();
	Coord<Number> getBottomLeftCoord();
	Coord<Number> getBottomRightCoord();
	void setMapCoords(Coord<Number> tl, Coord<Number> tr, Coord<Number> bl, Coord<Number> br);
	
	boolean isDimensionsProvided();
	Coord<Bounds<Number>> getRealDimensions();
	SISize getRealDimensionUnits();
	
}