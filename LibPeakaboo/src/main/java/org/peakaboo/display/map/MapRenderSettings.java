package org.peakaboo.display.map;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.display.map.modes.MapDisplayMode;

import cyclops.Coord;
import cyclops.SISize;

public class MapRenderSettings {

	public int dataWidth, dataHeight;
	public int interpolatedWidth, interpolatedHeight;
	
	public boolean showDatasetTitle = false;
	public String datasetTitle = "";
	
	public boolean showMapTitle = false;
	public String mapTitle = "";
	
	public boolean logTransform = false;
	public MapScaleMode scalemode = MapScaleMode.ABSOLUTE;
	public boolean monochrome = false;
	public boolean contours = false;
	public int contourSteps = 15;
	public float overlayLowCutoff = 0f;
	
	public boolean showScaleBar = false;
	
	public MapDisplayMode mode = MapDisplayMode.COMPOSITE;
	
	
	public boolean drawCoord = false;
	public Coord<Number> coordLoXLoY, coordHiXLoY, coordLoXHiY, coordHiXHiY;
	public SISize physicalUnits;
	public boolean physicalCoord = false;
	
	public boolean showSpectrum = false;
	public int spectrumHeight = 15;
	public String spectrumTitle = "";
	
	public boolean screenOrientation = false;
	
	public List<Integer> selectedPoints = new ArrayList<>();
	
	public CalibrationProfile calibrationProfile = new CalibrationProfile();

	
	
	
	
}
