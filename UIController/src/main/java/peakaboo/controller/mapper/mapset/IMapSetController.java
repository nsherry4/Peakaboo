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

	void setInterpolation(int passes);
	int getInterpolation();

	void setDataHeight(int height);
	int getDataHeight();
	void setDataWidth(int width);
	int getDataWidth();
	
	int getInterpolatedHeight();
	int getInterpolatedWidth();

	void setContours(boolean contours);
	boolean getContours();

	void setSpectrumSteps(int steps);
	int getSpectrumSteps();

	void setMonochrome(boolean mono);
	boolean getMonochrome();

	void setShowSpectrum(boolean show);
	boolean getShowSpectrum();

	void setShowTitle(boolean show);
	boolean getShowTitle();

	void setShowDatasetTitle(boolean show);
	boolean getShowDatasetTitle();

	void setShowCoords(boolean show);
	boolean getShowCoords();

	String getDatasetTitle();
	void setDatasetTitle(String name);


	
	
	List<Integer> getBadPoints();
	
	
	boolean isValidPoint(Coord<Integer> mapCoord);

	
	Coord<Number> getTopLeftCoord();
	Coord<Number> getTopRightCoord();
	Coord<Number> getBottomLeftCoord();
	Coord<Number> getBottomRightCoord();
	void setMapCoords(Coord<Number> tl, Coord<Number> tr, Coord<Number> bl, Coord<Number> br);
	boolean getDrawCoords();
	void setDrawCoords(boolean draw);
	
	boolean isDimensionsProvided();
	Coord<Bounds<Number>> getRealDimensions();
	SISize getRealDimensionUnits();
	
}