package peakaboo.controller.mapper.mapset;

import java.util.ArrayList;
import java.util.List;

import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.mapping.results.MapResultSet;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.ISpectrum;
import scitypes.SISize;
import scitypes.Spectrum;


public class MapSetController extends EventfulType<String> implements IMapSetController
{

	MapSetMapData 			mapModel;
	MapSetPreferences		mapPreferences;

	
	public MapSetController()
	{
		mapModel = new MapSetMapData();
		mapPreferences = new MapSetPreferences();
	}
	
	
	/**
	 * Copy constructor does not copy the actual map data, but
	 * only the user preferences such as interpolation. Each new 
	 * MapSetController will have to be given a model or data with
	 * which to construct a model.
	 * @param copy
	 */
	public MapSetController(MapSetController copy)
	{
		mapModel = new MapSetMapData();
		mapPreferences = new MapSetPreferences(copy.mapPreferences);
	}
	
	
	public void setMapData(
			MapResultSet data,
			String datasetName,
			List<Integer> badPoints
	)
	{
		
		mapModel.mapResults = data;
		mapModel.datasetTitle = datasetName;
		mapModel.badPoints = badPoints;
		
		mapModel.dimensionsProvided = false;
		mapModel.dataDimensions = new Coord<Integer>(data.getMap(0).data.size(), 1);
		mapModel.realDimensions = null;
		mapModel.realDimensionsUnits = null;
		
		updateListeners(UpdateType.DATA.toString());
		
	}
	
	public void setMapData(
			MapResultSet data,
			String datasetName,
			Coord<Integer> dataDimensions,
			Coord<Bounds<Number>> realDimensions,
			SISize realDimensionsUnits,
			List<Integer> badPoints
	)
	{
	
		
		mapModel.mapResults = data;
		mapModel.datasetTitle = datasetName;
		mapModel.badPoints = badPoints;
		
		mapModel.dataDimensions = new Coord<>(dataDimensions);
		mapModel.originalDimensions = dataDimensions;
		mapModel.dimensionsProvided = true;
		mapModel.realDimensions = realDimensions;
		mapModel.realDimensionsUnits = realDimensionsUnits;

		updateListeners(UpdateType.DATA.toString());

	}
	
	public int getMapSize()
	{
		return mapModel.mapSize();
	}

	
	

	// interpolation
	public void setInterpolation(int passes)
	{
		int side, newside;
		while (true) {
			
			side = (int)Math.sqrt( getDataHeight() * getDataWidth() );
			
			newside = (int)(side * Math.pow(2, passes));
		
			if (newside > 750) {
				passes--;
			} else {
				break;
			}
		
		}

		
		if (passes < 0) passes = 0;
		mapPreferences.interpolation = passes;
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	

	
	public int getInterpolation()
	{
		return mapPreferences.interpolation;
	}
	

	// data height and width
	public void setDataHeight(int height)
	{

		if (getDataWidth() * height > mapModel.mapSize()) height = mapModel.mapSize() / getDataWidth();
		if (height < 1) height = 1;

		mapModel.dataDimensions.y = height;
		
		setInterpolation(mapPreferences.interpolation);
		
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}


	public int getDataHeight()
	{
		return mapModel.dataDimensions.y;
	}


	public void setDataWidth(int width)
	{

		if (getDataHeight() * width > mapModel.mapSize()) width = mapModel.mapSize() / getDataHeight();
		if (width < 1) width = 1;

		mapModel.dataDimensions.x = width;
		
		setInterpolation(mapPreferences.interpolation);
		
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}

	public int getDataWidth()
	{
		return mapModel.dataDimensions.x;
	}
	
	
	public int getOriginalDataHeight() {
		return mapModel.originalDimensions.y;
	}
	public int getOriginalDataWidth() {
		return mapModel.originalDimensions.x;
	}
	
	
	public boolean guessDataDimensions() {
		Spectrum all = mapModel.mapResults.sumAllTransitionSeriesMaps();
		Spectrum deltas = new ISpectrum(all);
		
		
		//compute first order deltas
		for (int i = 1; i < deltas.size(); i++) {
			float delta = Math.abs(deltas.get(i) - deltas.get(i-1));
			deltas.set(i-1, delta);
		}
		
		//compute second order deltas. We're not looking for steep changes, 
		//we're looking for sudden discontinuities, the change in the change.
		for (int i = 1; i < deltas.size(); i++) {
			float delta = (float)Math.sqrt(Math.abs(deltas.get(i) - deltas.get(i-1)));
			deltas.set(i-1, delta);
		}
		
		//find the highest average edge delta
		int bestX = 0, bestY = 0;
		float bestDelta = Float.MAX_VALUE;
		int min = (int) Math.max(Math.sqrt(all.size()) / 15, 2); //don't consider dimensions that are too small
		for (int x = min; x <= all.size() / min; x++) {
			
			float delta;
			int y;
			if (all.size() % x == 0) {
				y = all.size() / x;
				delta = getDimensionScore(all, x, y);
			} else {
				y = (int)Math.ceil(all.size() / (float)x);
				delta = getDimensionScore(all, x, y); //include the last incomplete row
			}
			
			//System.out.println("x=" + x + ", y=" + y + ", delta=" + delta);
			
			if (delta < bestDelta) {
				bestX = x;
				bestY = y;
				bestDelta = delta;
			}
			
			
		}
		
		if (bestX > 0 && bestY > 0) {
			//set to 1 first to prevent bounds check failures for map-too-large
			setDataWidth(1);
			setDataHeight(1);
			setDataWidth(bestX);
			setDataHeight(bestY);
			return true;
		} else {
			return false;
		}
		
	}
	
	
	//helper for guessDataDimensions, calculates the deltas along the wrapping 
	//left-hand edge of a map between the end of one row and the start of the 
	//next. Higher values should indicate the dimensions are correct and the 
	//two sets of points are not next to each other.
	private float getDimensionScore(Spectrum map, int width, int height) {
		return map2dDelta(map2dDelta(map, width, height), width, height).sum();
	}
	
	private Spectrum map2dDelta(Spectrum map, int width, int height) {
		Spectrum deltas = new ISpectrum(map.size());
		float delta = 0;
		float value = 0;
		int count = 0;
		int dind;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y*width+x >= map.size()) break;
				value = map.get(y*width+x);
				count = 0;
				delta = 0;
				
				dind = y*width+(x-1);
				if (x > 0) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				dind = (y-1)*width+x;
				if (y > 0) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}

				dind = y*width+(x+1);
				if (x < width-1 && dind < map.size()) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				dind = (y+1)*width+x;
				if (y < height-1 && dind < map.size()) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				delta /= (float)count;
				deltas.set(y*width+x, delta);

			}
		}
		
		return deltas;
		
	}
	
	


	// contours
	public void setContours(boolean contours)
	{
		mapPreferences.contour = contours;
				
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getContours()
	{
		return mapPreferences.contour;
	}


	// spectrum
	public void setSpectrumSteps(int steps)
	{
		if (steps > 25) steps = 25;
		if (steps > 0)
		{
			mapPreferences.spectrumSteps = steps;
		}
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public int getSpectrumSteps()
	{
		return mapPreferences.spectrumSteps;
	}


	public void setMonochrome(boolean mono)
	{
		mapPreferences.monochrome = mono;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getMonochrome()
	{
		return mapPreferences.monochrome;
	}

	

	public void setShowSpectrum(boolean show)
	{
		mapPreferences.drawSpectrum = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowSpectrum()
	{
		return mapPreferences.drawSpectrum;
	}


	public void setShowTitle(boolean show)
	{
		mapPreferences.drawTitle = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowTitle()
	{
		return mapPreferences.drawTitle;
	}


	public void setShowDatasetTitle(boolean show)
	{
		mapPreferences.showDataSetTitle = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowDatasetTitle()
	{
		return mapPreferences.showDataSetTitle;
	}


	public void setShowCoords(boolean show)
	{
		mapPreferences.drawCoordinates = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowCoords()
	{
		return mapPreferences.drawCoordinates;
	}
	
	public boolean isDimensionsProvided()
	{
		return mapModel.dimensionsProvided;
	}


	public List<Integer> getBadPoints()
	{
		return new ArrayList<>(mapModel.badPoints);
	}


	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < getDataWidth() && mapCoord.y >= 0 && mapCoord.y < getDataHeight());
	}


	public String getDatasetTitle()
	{
		return mapModel.datasetTitle;
	}


	public void setDatasetTitle(String name)
	{
		mapModel.datasetTitle = name;
	}


	public void setMapCoords(Coord<Number> tl, Coord<Number> tr, Coord<Number> bl, Coord<Number> br)
	{
		mapModel.topLeftCoord = tl;
		mapModel.topRightCoord = tr;
		mapModel.bottomLeftCoord = bl;
		mapModel.bottomRightCoord = br;
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		return mapModel.realDimensions;
	}
	
	
	
	public int getInterpolatedHeight()
	{
		
		int height = getDataHeight();
		
		for (int i = 0; i < getInterpolation(); i++)
		{
			height = height * 2 - 1;
		}
		
		return height;
		
	}


	public int getInterpolatedWidth()
	{
		int width = getDataWidth();
		
		for (int i = 0; i < getInterpolation(); i++)
		{
			width = width * 2 - 1;
		}
		
		return width;
		
	}
	
	
	public Coord<Number> getTopLeftCoord()
	{
		return mapModel.topLeftCoord;
	}
	public Coord<Number> getTopRightCoord()
	{
		return mapModel.topRightCoord;
	}
	public Coord<Number> getBottomLeftCoord()
	{
		return mapModel.bottomLeftCoord;
	}
	public Coord<Number> getBottomRightCoord()
	{
		return mapModel.bottomRightCoord;
	}
	
	
	public SISize getRealDimensionUnits()
	{
		return mapModel.realDimensionsUnits;
	}
	
	public boolean getDrawCoords()
	{
		return mapPreferences.drawCoordinates;
	}
	public void setDrawCoords(boolean draw)
	{
		mapPreferences.drawCoordinates = draw;
	}


	public MapResultSet getMapResultSet()
	{
		return mapModel.mapResults;
	}
	
}
