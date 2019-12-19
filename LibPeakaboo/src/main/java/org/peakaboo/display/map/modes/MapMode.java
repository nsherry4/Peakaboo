package org.peakaboo.display.map.modes;

import java.lang.ref.SoftReference;
import java.util.List;

import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.map.MapDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;

public abstract class MapMode {

	protected DrawingRequest dr;
	protected MapDrawing map;
	
	private SoftReference<SelectionMaskPainter> selectionPainterRef = new SoftReference<>(null);
	
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
		
		size = new Coord<>((int)Math.round(width), (int)Math.round(height));
		dr.imageWidth = (float)size.x;
		dr.imageHeight = (float)size.y;
		
		return size;
		
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
	
	protected SelectionMaskPainter getSelectionPainter(PaletteColour colour, List<Integer> points, int width, int height) {
		SelectionMaskPainter selectionPainter = selectionPainterRef.get();
		if (selectionPainter == null) {
			selectionPainter = new SelectionMaskPainter(colour, points, width, height);
			selectionPainterRef = new SoftReference<>(selectionPainter);
		} else {
			selectionPainter.configure(width, height, points);
		}
		return selectionPainter;
	}
	
	protected static SpectrumCoordsAxisPainter getSpectrumPainter(MapRenderSettings settings, int spectrumSteps, List<AbstractPalette> paletteList) {
		return new SpectrumCoordsAxisPainter (

				settings.drawCoord,
				settings.coordLoXHiY,
				settings.coordHiXHiY,
				settings.coordLoXLoY,
				settings.coordHiXLoY,
				settings.physicalUnits,

				settings.showSpectrum,
				settings.spectrumHeight,
				spectrumSteps,
				paletteList,

				settings.physicalCoord,
				settings.showScaleBar
			);
	}
	
	protected SpectrumCoordsAxisPainter getSpectrumPainter(
			MapRenderSettings settings, 
			int spectrumSteps, 
			List<AbstractPalette> paletteList, 
			boolean hasNegatives, 
			List<Pair<Float, String>> spectrumMarkers
		) {
		
		return new SpectrumCoordsAxisPainter (
			settings.drawCoord,
			settings.coordLoXHiY,
			settings.coordHiXHiY,
			settings.coordLoXLoY,
			settings.coordHiXLoY,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,
			spectrumSteps,
			paletteList,

			settings.physicalCoord,
			settings.showScaleBar,
			1,
			hasNegatives,
			spectrumMarkers
		);
	}
	
	protected void setupTitleAxisPainters(MapRenderSettings settings, List<AxisPainter> axisPainters) {
		//if we're showing a dataset title, add a title axis painter to put a title on the top
		if (settings.showDatasetTitle)
		{
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, settings.datasetTitle, null));
		}

		//if we're map title, add a title axis painter to put a title on the bottom
		if (settings.showMapTitle)
		{
			String mapTitle = settings.mapTitle;
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, null, mapTitle));
		}
	}
	
	public abstract void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps);
	
	public abstract MapModes getMode();

	public abstract void invalidate();
	
}
