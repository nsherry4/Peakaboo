package org.peakaboo.ui.swing.mapping;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.settings.MapViewSettings;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.Mapper;

import cyclops.Coord;
import cyclops.visualization.Surface;
import cyclops.visualization.backend.awt.GraphicsPanel;


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
	protected void drawGraphics(Surface backend, Coord<Integer> size)
	{
		try {
			drawMap(backend, size);
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
		return mapper.getCoordinate(x, y, allowOutOfBounds);

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

	
	
	private void drawMap(Surface context, Coord<Integer> size) {
				
		MapRenderSettings settings = controller.getRenderSettings();
		MapRenderData data = controller.getMapRenderData();
		mapper.draw(data, settings, context, size);
		
	}
	
	
	public void setNeedsRedraw() {
		mapper.setNeedsRedraw();
	}

	


	
}
