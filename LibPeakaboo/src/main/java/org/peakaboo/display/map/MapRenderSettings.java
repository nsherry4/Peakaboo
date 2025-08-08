package org.peakaboo.display.map;

import org.peakaboo.app.Settings;
import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.display.map.modes.MapModeRegistry;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.cyclops.visualization.palette.Gradient;
import org.peakaboo.framework.cyclops.visualization.palette.Gradients;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

import it.unimi.dsi.fastutil.ints.IntArrayList;


public class MapRenderSettings {

	public int userDataWidth, userDataHeight;
	public int filteredDataWidth, filteredDataHeight;
	
	public boolean showDatasetTitle = false;
	public String datasetTitle = "";
	
	public boolean showMapTitle = false;
	public String mapTitle = "";
	
	public MapScaleMode scalemode = MapScaleMode.ABSOLUTE;
	public Gradient gradient = Settings.getDefaultMapPalette();
	public boolean darkmode = false;
	public boolean contours = false;
	public int contourSteps = 15;
	
	public boolean showScaleBar = false;
	
	public String mode = MapModeRegistry.get().defaultType();
	
	
	public boolean drawCoord = false;
	public Coord<Number> coordLoXLoY, coordHiXLoY, coordLoXHiY, coordHiXHiY;
	public SISize physicalUnits;
	public boolean physicalCoord = false;
	
	public boolean showSpectrum = false;
	public int spectrumHeight = 10;
	public String spectrumTitle = "";
		
	public IntArrayList selectedPoints = new IntArrayList();
	
	public DetectorProfile detectorProfile = new BasicDetectorProfile();

	
	public PaletteColour getFg() {
		if (darkmode) {
			return new PaletteColour(0xffe0e0e0);
		} else {
			return new PaletteColour(0xff000000);
		}
	}

	public PaletteColour getBg() {
		if (darkmode) {
			return new PaletteColour(0xff202020);
		} else {
			return new PaletteColour(0xffffffff);
		}
	}

	
}
