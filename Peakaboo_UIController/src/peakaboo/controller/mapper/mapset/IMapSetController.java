package peakaboo.controller.mapper.mapset;



import java.util.List;

import peakaboo.mapping.results.MapResultSet;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;



public interface IMapSetController
{

	public void setMapData(
			MapResultSet data,
			String datasetName,
			Coord<Integer> dataDimensions,
			Coord<Bounds<Number>> realDimensions,
			SISize realDimensionsUnits,
			List<Integer> badPoints
	);
	public void setMapData(
			MapResultSet data,
			String datasetName,
			List<Integer> badPoints
	);
	
	public MapResultSet getMapResultSet();
	public int getMapSize();

	public void setInterpolation(int passes);
	public int getInterpolation();

	public void setDataHeight(int height);
	public int getDataHeight();
	public void setDataWidth(int width);
	public int getDataWidth();
	
	public abstract int getInterpolatedHeight();
	public abstract int getInterpolatedWidth();

	public abstract void setContours(boolean contours);
	public abstract boolean getContours();

	public void setSpectrumSteps(int steps);
	public int getSpectrumSteps();

	public void setMonochrome(boolean mono);
	public boolean getMonochrome();

	public void setShowSpectrum(boolean show);
	public boolean getShowSpectrum();

	public void setShowTitle(boolean show);
	public boolean getShowTitle();

	public void setShowDatasetTitle(boolean show);
	public boolean getShowDatasetTitle();

	public void setShowCoords(boolean show);
	public boolean getShowCoords();

	public String getDatasetTitle();
	public void setDatasetTitle(String name);


	
	
	public List<Integer> getBadPoints();
	
	
	public boolean isValidPoint(Coord<Integer> mapCoord);

	
	public Coord<Number> getTopLeftCoord();
	public Coord<Number> getTopRightCoord();
	public Coord<Number> getBottomLeftCoord();
	public Coord<Number> getBottomRightCoord();
	public void setMapCoords(Coord<Number> tl, Coord<Number> tr, Coord<Number> bl, Coord<Number> br);
	public boolean getDrawCoords();
	public void setDrawCoords(boolean draw);
	
	public boolean isDimensionsProvided();
	public Coord<Bounds<Number>> getRealDimensions();
	public SISize getRealDimensionUnits();
	
}