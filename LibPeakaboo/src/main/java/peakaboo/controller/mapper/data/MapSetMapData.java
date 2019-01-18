package peakaboo.controller.mapper.data;


import java.util.ArrayList;
import java.util.List;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.SISize;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.mapping.results.AreaMapSet;



public class MapSetMapData
{

	
	//Map data
	public Coord<Bounds<Number>>	realDimensions;
	public SISize					realDimensionsUnits;
	
	
	public Coord<Integer>			originalDimensions	= new Coord<Integer>(1, 1);
	public boolean					originalDimensionsProvided = false;
	
	public AreaMapSet				areaMaps			= null;
	public String					datasetTitle		= "";
	
	public List<Integer>			badPoints			= new ArrayList<Integer>();
	public CalibrationProfile 		calibrationProfile	= new CalibrationProfile();
	

	public int mapSize()
	{
		return areaMaps.size();
	}
	
}
