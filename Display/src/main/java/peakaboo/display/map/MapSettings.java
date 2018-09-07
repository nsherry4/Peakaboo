package peakaboo.display.map;

import java.util.ArrayList;
import java.util.List;

import peakaboo.controller.mapper.settings.MapDisplayMode;
import peakaboo.controller.mapper.settings.MapScaleMode;
import scidraw.drawing.ViewTransform;
import scidraw.drawing.map.painters.MapPainter;
import scitypes.Coord;
import scitypes.SISize;

public class MapSettings {

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
		
	public MapDisplayMode mode = MapDisplayMode.COMPOSITE;
	
	
	public boolean drawCoord = false;
	public Coord<Number> coordTL, coordTR, coordBL, coordBR;
	public SISize physicalUnits;
	public boolean physicalCoord = false;
	
	public boolean showSpectrum = false;
	public int spectrumHeight = 15;
	public String spectrumTitle = "";
	
	
	public List<MapPainter> painters = new ArrayList<>();
	
	
}
