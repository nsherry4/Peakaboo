package peakaboo.ui.swing.mapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.controller.mapper.settings.MapScaleMode;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.mapper.settings.PointsSelection;
import peakaboo.display.map.MapData;
import peakaboo.display.map.MapSettings;
import peakaboo.display.map.Mapper;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.map.painters.SelectionMaskPainter;
import scidraw.swing.GraphicsPanel;
import scitypes.Bounds;
import scitypes.Coord;


class MapCanvas extends GraphicsPanel
{

	private MappingController 		controller;
	private MapViewSettings			viewSettings;
	
	private Mapper mapper;
	
	private static final int	SPECTRUM_HEIGHT = 15;
	
	MapCanvas(MappingController controller)
	{
		this.controller = controller;
		this.viewSettings = controller.getSettings().getView();
		
		mapper = new Mapper();
				
	}
	
	@Override
	protected void drawGraphics(Surface backend, boolean vector, Dimension size)
	{
		try {
			drawMap(backend, vector, size);
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Unable to draw map", e);
		}
	}

	@Override
	public float getUsedHeight()
	{
		return getUsedHeight(1f);
	}

	@Override
	public float getUsedWidth()
	{
		return getUsedWidth(1f);
	}

	@Override
	public float getUsedWidth(float zoom) {
		return mapper.getMap().calcTotalSize().x * zoom;
	}

	@Override
	public float getUsedHeight(float zoom) {
		return mapper.getMap().calcTotalSize().y * zoom;
	}
	
	
	
	
	
	

	public Coord<Integer> getMapCoordinateAtPoint(float x, float y, boolean allowOutOfBounds)
	{

		if (mapper == null) return null;
		return mapper.getMap().getMapCoordinateAtPoint(x, y, allowOutOfBounds);

	}

	
	

	
	public void updateCanvasSize()
	{
			
		//Width
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (parentWidth * viewSettings.getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		
		
		//Height
		double parentHeight = 1.0;
		if (this.getParent() != null)
		{
			parentHeight = this.getParent().getHeight();
		}

		int newHeight = (int) (parentHeight * viewSettings.getZoom());
		if (newHeight < parentHeight) newHeight = (int) parentHeight;
		
		
		//Generate new sizes
		Rectangle oldView = this.getVisibleRect();
		Dimension oldSize = getPreferredSize();
		Dimension newSize = new Dimension(newWidth, newHeight);
		Rectangle newView = new Rectangle(oldView);
		

		//Ratio of new size to old one.
		float dx = (float)newSize.width / (float)oldSize.width;
		float dy = (float)newSize.height / (float)oldSize.height;

		//Scale view by size ratio
		newView.x = (int) (oldView.x * dx);
		newView.y = (int) (oldView.y * dy);

		//Set new size and update
		this.setPreferredSize(newSize);
		this.revalidate();
		this.scrollRectToVisible(newView);
		
		


	}

	
	
	private void drawMap(Surface context, boolean vector, Dimension size)
	{
		
		//TODO: Why is this here, instead of in the controller 
		//along with all the settings it's accessing? Does it need
		//to get run with every draw?
		if (controller.mapsController.getRealDimensions() != null)
		{

			Coord<Bounds<Number>> realDims = controller.mapsController.getRealDimensions();
			
			controller.mapsController.setMapCoords(
					new Coord<Number>( realDims.x.start, 	realDims.y.end),
					new Coord<Number>( realDims.x.end, 		realDims.y.end), 
					new Coord<Number>( realDims.x.start,	realDims.y.start), 
					new Coord<Number>( realDims.x.end,		realDims.y.start) 
					
					
				);

		}
		else
		{

			controller.mapsController.setMapCoords(
					new Coord<Number>(1, viewSettings.getDataHeight()),
					new Coord<Number>(viewSettings.getDataWidth(), viewSettings.getDataHeight()),
					new Coord<Number>(1, 1), 
					new Coord<Number>(viewSettings.getDataWidth(), 1)					
				);
		}
		
		
		MapSettings settings = new MapSettings();
		MapData data = new MapData();
		
		
		settings.dataWidth = viewSettings.getDataWidth(); 
		settings.dataHeight = viewSettings.getDataHeight();
		settings.interpolatedWidth = viewSettings.getInterpolatedWidth();
		settings.interpolatedHeight = viewSettings.getInterpolatedHeight();
		
		settings.showDatasetTitle = viewSettings.getShowDatasetTitle();
		settings.datasetTitle = controller.mapsController.getDatasetTitle();
		
		settings.showMapTitle = viewSettings.getShowTitle();
		settings.mapTitle = controller.getSettings().getMapFittings().mapLongTitle();
		
		settings.logTransform = controller.getSettings().getMapFittings().isLogView();
		settings.scalemode = controller.getSettings().getMapFittings().getMapScaleMode();
		settings.monochrome = viewSettings.getMonochrome();
		settings.contours = viewSettings.getContours();
		settings.contourSteps = viewSettings.getSpectrumSteps();
			
		settings.mode = controller.getSettings().getMapFittings().getMapDisplayMode();
		
		
		settings.drawCoord = viewSettings.getDrawCoords();
		settings.coordTL = controller.mapsController.getTopLeftCoord();
		settings.coordTR = controller.mapsController.getTopRightCoord();
		settings.coordBL = controller.mapsController.getBottomLeftCoord();
		settings.coordBR = controller.mapsController.getBottomRightCoord();
		settings.physicalUnits = controller.mapsController.getRealDimensionUnits();
		settings.physicalCoord = controller.mapsController.getRealDimensions() != null;
		
		settings.showSpectrum = viewSettings.getShowSpectrum();
		settings.spectrumHeight = SPECTRUM_HEIGHT;
		

		
		
		//There should only ever be one selection active at a time
		AreaSelection areaSelection = controller.getSettings().getAreaSelection();
		if (areaSelection.hasSelection()) {
			settings.painters.add(new SelectionMaskPainter(Color.white, areaSelection.getPoints(), settings.dataWidth, settings.dataHeight));
		}
		
		PointsSelection pointsSelection = controller.getSettings().getPointsSelection();
		if (pointsSelection.hasSelection()) {
			settings.painters.add(new SelectionMaskPainter(Color.white, pointsSelection.getPoints(), settings.dataWidth, settings.dataHeight));
		}
		
		
		
		switch (settings.mode) {
		case COMPOSITE:
			data.compositeData = controller.getSettings().getMapFittings().getCompositeMapData();
			settings.spectrumTitle = controller.getSettings().getMapFittings().isLogView() ? "Log Scale Intensity (counts)" : "Intensity (counts)";
			break;
		case OVERLAY:
			data.overlayData = controller.getSettings().getMapFittings().getOverlayMapData();
			settings.spectrumTitle = "Colour" +
					(controller.getSettings().getMapFittings().isLogView() ? " (Log Scale)" : "") + 
					(controller.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Colours scaled independently" : "");
			break;
		case RATIO:
			data.ratioData = controller.getSettings().getMapFittings().getRatioMapData();
			settings.spectrumTitle = "Intensity (ratio)" + (controller.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Ratio sides scaled independently" : "");
			break;
		}
		
		data.maxIntensity = controller.getSettings().getMapFittings().sumAllTransitionSeriesMaps().max();
		
		
		
		mapper.draw(data, settings, context, vector, size);
		
		
		return;
		
	}
	
	
	public void setNeedsRedraw() {
		mapper.setNeedsRedraw();
	}


	
}
