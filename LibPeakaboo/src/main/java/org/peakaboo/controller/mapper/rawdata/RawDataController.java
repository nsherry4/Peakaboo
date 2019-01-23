package org.peakaboo.controller.mapper.rawdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.SISize;
import cyclops.Spectrum;
import eventful.EventfulType;
import plural.streams.StreamExecutor;


public class RawDataController extends EventfulType<String>
{

	RawDataModel mapModel;
	
	
	public RawDataController()
	{
		mapModel = new RawDataModel();
	}
	


	/**
	 * Sets the map's data model. dataDimensions, realDimensions, realDimensionsUnits may be null
	 * @param calibrationProfile 
	 */
	public void setMapData(
			RawMapSet data,
			String datasetName,
			List<Integer> badPoints,
			Coord<Integer> dataDimensions,
			Coord<Bounds<Number>> realDimensions,
			SISize realDimensionsUnits, 
			CalibrationProfile calibrationProfile
			
	)
	{
	
		
		mapModel.mapResults = data;
		mapModel.datasetTitle = datasetName;
		mapModel.badPoints = badPoints;
		
		mapModel.originalDimensions = dataDimensions;
		mapModel.originalDimensionsProvided = dataDimensions != null;
		mapModel.realDimensions = realDimensions;
		mapModel.realDimensionsUnits = realDimensionsUnits;
		mapModel.calibrationProfile = calibrationProfile;

		updateListeners(UpdateType.DATA.toString());

	}
	
	public int getMapSize()
	{
		return mapModel.mapSize();
	}

	
	


	

	

	
	public Coord<Integer> getOriginalDataDimensions() {
		return mapModel.originalDimensions;
	}
	
	public boolean hasOriginalDataDimensions() {
		return mapModel.originalDimensionsProvided;
	}
	
	public int getOriginalDataHeight() {
		return mapModel.originalDimensions.y;
	}
	public int getOriginalDataWidth() {
		return mapModel.originalDimensions.x;
	}
	
	
	



	public List<Integer> getBadPoints()
	{
		return new ArrayList<>(mapModel.badPoints);
	}


	public String getDatasetTitle()
	{
		return mapModel.datasetTitle;
	}


	public void setDatasetTitle(String name)
	{
		mapModel.datasetTitle = name;
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		return mapModel.realDimensions;
	}
	
	
	public SISize getRealDimensionUnits()
	{
		return mapModel.realDimensionsUnits;
	}
	

	public RawMapSet getMapResultSet()
	{
		return mapModel.mapResults;
	}

	public CalibrationProfile getCalibrationProfile() {
		return mapModel.calibrationProfile;
	}
		
}
