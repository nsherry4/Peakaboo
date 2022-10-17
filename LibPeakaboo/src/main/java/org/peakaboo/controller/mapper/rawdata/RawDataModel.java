package org.peakaboo.controller.mapper.rawdata;


import java.util.ArrayList;
import java.util.List;

import org.peakaboo.calibration.BasicCalibrationProfile;
import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.mapping.rawmap.RawMapSet;



public class RawDataModel
{

	
	//Map data
	public Coord<Bounds<Number>>	realDimensions;
	public SISize					realDimensionsUnits;
	
	
	public Coord<Integer>			originalDimensions	= new Coord<>(1, 1);
	public boolean					originalDimensionsProvided = false;
	
	public RawMapSet				mapResults			= null;
	public String					datasetTitle		= "";
	
	public List<Integer>			badPoints			= new ArrayList<>();
	public CalibrationProfile 		calibrationProfile	= new BasicCalibrationProfile();
	public DataSet 					sourceDataset		= null;
	

	public int mapSize()
	{
		return mapResults.size();
	}
	
}
