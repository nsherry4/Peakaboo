package org.peakaboo.display.map.modes;

import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.map.MapDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.TitleAxisPainter;

public abstract class MapMode {

	protected DrawingRequest dr;
	protected MapDrawing map;
	
	public MapMode() {
		this.dr = new DrawingRequest();
		map = new MapDrawing(null, dr);	
	}
	
	public Coord<Integer> setDimensions(MapRenderSettings settings, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		//need to set this up front so that calTotalSize has the right dimensions to work with
		dr.dataHeight = settings.filteredDataHeight;
		dr.dataWidth = settings.filteredDataWidth;
		
		double width = 0;
		double height = 0;
		
		if (size != null) {
			width = size.x;
			height = size.y;
		}
		
		//Auto-detect dimensions
		if (size == null || (size.x == 0 && size.y == 0)) {
			dr.imageWidth = 1000;
			dr.imageHeight = 1000;
			Coord<Float> newsize = map.calcTotalSize();
			width = newsize.x;
			height = newsize.y;
		}
		else if (size.x == 0) {
			dr.imageWidth = dr.imageHeight * 10;
			width = map.calcTotalSize().x;
			
		}
		else if (size.y == 0) {
			dr.imageHeight = dr.imageWidth * 10;
			height = map.calcTotalSize().y;
		}
		
		size = new Coord<Integer>((int)Math.round(width), (int)Math.round(height));
		dr.imageWidth = (float)size.x;
		dr.imageHeight = (float)size.y;
		
		return size;
		
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds) {
		return map.getMapCoordinateAtPoint(x, y, allowOutOfBounds);
	}
	
	public MapDrawing getMap() {
		return map;
	}

	protected AxisPainter getDescriptionPainter(MapRenderSettings settings) {
		String title = settings.spectrumTitle;
		if (!settings.calibrationProfile.isEmpty()) {
			title += " calibrated with " + settings.calibrationProfile.getName();	
		}
		return new TitleAxisPainter(TitleAxisPainter.SCALE_TEXT, null, null, null, title);
	}
	
	public abstract void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps);
	
	//TODO: Make this a String? How to make it expandable?
	public abstract MapDisplayMode getMode();
	
	
	
	public abstract void invalidate();
	
}
