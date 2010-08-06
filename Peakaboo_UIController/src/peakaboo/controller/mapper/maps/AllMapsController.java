package peakaboo.controller.mapper.maps;

import java.util.List;

import peakaboo.controller.mapper.MapController.UpdateType;
import peakaboo.mapping.results.MapResultSet;
import scitypes.Coord;
import scitypes.SISize;
import eventful.EventfulType;
import fava.Fn;
import fava.Functions;
import fava.datatypes.Bounds;


public class AllMapsController extends EventfulType<String> implements IAllMapsController
{

	AllMapsModel 	mapModel;

	
	public AllMapsController()
	{
		mapModel = new AllMapsModel();
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
		
		mapModel.dataDimensions = dataDimensions;
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
		mapModel.interpolation = passes;
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	

	
	public int getInterpolation()
	{
		return mapModel.interpolation;
	}
	

	// data height and width
	public void setDataHeight(int height)
	{

		if (getDataWidth() * height > mapModel.mapSize()) height = mapModel.mapSize() / getDataWidth();
		if (height < 1) height = 1;

		mapModel.dataDimensions.y = height;
		
		setInterpolation(mapModel.interpolation);
		
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
		
		setInterpolation(mapModel.interpolation);
		
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}

	public int getDataWidth()
	{
		return mapModel.dataDimensions.x;
	}
	
	


	// contours
	public void setContours(boolean contours)
	{
		mapModel.contour = contours;
				
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getContours()
	{
		return mapModel.contour;
	}


	// spectrum
	public void setSpectrumSteps(int steps)
	{
		if (steps > 25) steps = 25;
		if (steps > 0)
		{
			mapModel.spectrumSteps = steps;
		}
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public int getSpectrumSteps()
	{
		return mapModel.spectrumSteps;
	}


	public void setMonochrome(boolean mono)
	{
		mapModel.monochrome = mono;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getMonochrome()
	{
		return mapModel.monochrome;
	}

	

	public void setShowSpectrum(boolean show)
	{
		mapModel.drawSpectrum = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowSpectrum()
	{
		return mapModel.drawSpectrum;
	}


	public void setShowTitle(boolean show)
	{
		mapModel.drawTitle = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowTitle()
	{
		return mapModel.drawTitle;
	}


	public void setShowDatasetTitle(boolean show)
	{
		mapModel.showDataSetTitle = show;
	}


	public boolean getShowDatasetTitle()
	{
		return mapModel.showDataSetTitle;
	}


	public void setShowCoords(boolean show)
	{
		mapModel.drawCoordinates = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowCoords()
	{
		return mapModel.drawCoordinates;
	}
	
	public boolean isDimensionsProvided()
	{
		return mapModel.dimensionsProvided;
	}


	public List<Integer> getBadPoints()
	{
		return Fn.map(mapModel.badPoints, Functions.<Integer>id());
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
		return mapModel.drawCoordinates;
	}
	public void setDrawCoords(boolean draw)
	{
		mapModel.drawCoordinates = draw;
	}


	public MapResultSet getMapResultSet()
	{
		return mapModel.mapResults;
	}
	
}
