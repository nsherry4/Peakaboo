package peakaboo.ui.swing.mapping;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.data.MapRenderData;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.controller.mapper.settings.MapRenderSettings;
import peakaboo.controller.mapper.settings.MapScaleMode;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.mapper.settings.PointsSelection;
import peakaboo.display.map.Mapper;
import scidraw.swing.GraphicsPanel;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.visualization.Surface;


class MapCanvas extends GraphicsPanel
{

	private MappingController 		controller;
	private MapViewSettings			viewSettings;
	
	private Mapper mapper;
	
	
	MapCanvas(MappingController controller)
	{
		this.controller = controller;
		this.viewSettings = controller.getSettings().getView();
		
		mapper = new Mapper();
				
	}
	
	@Override
	protected void drawGraphics(Surface backend, boolean vector, Coord<Integer> size)
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

	
	
	private void drawMap(Surface context, boolean vector, Coord<Integer> size)
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
		
		
		MapRenderSettings settings = controller.getRenderSettings();
		MapRenderData data = controller.getMapRenderData();
		
		mapper.draw(data, settings, context, vector, size);
		
		
		return;
		
	}
	
	
	public void setNeedsRedraw() {
		mapper.setNeedsRedraw();
	}

	


	
}
