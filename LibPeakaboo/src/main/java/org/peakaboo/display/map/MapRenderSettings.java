package org.peakaboo.display.map;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SISize;

public class MapRenderSettings {

	public int userDataWidth, userDataHeight;
	public int filteredDataWidth, filteredDataHeight;
	
	public boolean showDatasetTitle = false;
	public String datasetTitle = "";
	
	public boolean showMapTitle = false;
	public String mapTitle = "";
	
	public MapScaleMode scalemode = MapScaleMode.ABSOLUTE;
	public boolean monochrome = false;
	public boolean contours = false;
	public int contourSteps = 15;
	
	public boolean showScaleBar = false;
	
	public MapModes mode = MapModes.COMPOSITE;
	
	
	public boolean drawCoord = false;
	public Coord<Number> coordLoXLoY, coordHiXLoY, coordLoXHiY, coordHiXHiY;
	public SISize physicalUnits;
	public boolean physicalCoord = false;
	
	public boolean showSpectrum = false;
	public int spectrumHeight = 15;
	public String spectrumTitle = "";
		
	public List<Integer> selectedPoints = new ArrayList<>();
	
	public CalibrationProfile calibrationProfile = new CalibrationProfile();

	
	
	
	
}
