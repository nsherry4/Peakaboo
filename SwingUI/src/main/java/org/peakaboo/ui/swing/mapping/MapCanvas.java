package org.peakaboo.ui.swing.mapping;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.Mapper;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.GraphicsPanel;


public class MapCanvas extends GraphicsPanel
{

	private MappingController mapController;
	private MapSettingsController settingsController;
	private Mapper mapper;
	
	
	public MapCanvas(MappingController controller, boolean resizable)
	{
		this.mapController = controller;
		this.settingsController = controller.getSettings();
		
		mapper = new Mapper();
		
		controller.addListener(t -> {
			if (t == MapUpdateType.SELECTION) {
				/*
				 * data hasn't changed but we need to recomposite the image, so we pass false
				 * for parameter 'deep'
				 */
				setNeedsRedraw(false);
			}
			else {
				setNeedsRedraw(true);
			}

			//don't repaint right away, give the other event listeners time to catch up
			SwingUtilities.invokeLater(() -> {
				if (resizable) {
					updateCanvasSize();
				}
				repaint();
			});
		});
		
				
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
		Coord<Integer> canvasSize = new Coord<>(getWidth(), getHeight());
		return mapper.getCoordinate(x, y, allowOutOfBounds, canvasSize);

	}

	
	

	
	public void updateCanvasSize()
	{
		updateCanvasSize(null);
	}
	
	public void updateCanvasSize(Coord<Integer> zoomCenter)
	{
			
		//Width
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (parentWidth * settingsController.getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		
		
		//Height
		double parentHeight = 1.0;
		if (this.getParent() != null)
		{
			parentHeight = this.getParent().getHeight();
		}

		int newHeight = (int) (parentHeight * settingsController.getZoom());
		if (newHeight < parentHeight) newHeight = (int) parentHeight;
		
		
		//Generate new sizes
		Rectangle oldView = this.getVisibleRect();
		Dimension oldSize = getPreferredSize();
		Dimension newSize = new Dimension(newWidth, newHeight);
		Rectangle newView = new Rectangle(oldView);
		

		//Ratio of new size to old one.
		float dx = (float)newSize.width / (float)oldSize.width;
		float dy = (float)newSize.height / (float)oldSize.height;

		if (zoomCenter != null) {
			// Zoom from the specified center point
			int centerX = zoomCenter.x;
			int centerY = zoomCenter.y;
			
			// Calculate the new viewport position to keep the zoom center in the same relative position
			newView.x = (int) (centerX * dx - (centerX - oldView.x));
			newView.y = (int) (centerY * dy - (centerY - oldView.y));
		} else {
			// Default behavior: scale view by size ratio from top-left
			newView.x = (int) (oldView.x * dx);
			newView.y = (int) (oldView.y * dy);
		}

		//Set new size and update
		this.setPreferredSize(newSize);
		this.revalidate();
		this.scrollRectToVisible(newView);
		
		// Delay the redraw operations to ensure canvas size is fully updated
		SwingUtilities.invokeLater(() -> {
			// Force redraw with new centering when size changes
			setNeedsRedraw(true);
			// Also force buffer cache clearing by invalidating the mapper
			mapper.setNeedsRedraw(true);
			repaint();
		});
		
		


	}

	
	
	private void drawMap(Surface context, Coord<Integer> size) {
				
		MapRenderSettings settings = mapController.getRenderSettings();
		MapRenderData data = mapController.getMapRenderData();
		mapper.draw(data, settings, context, size);
		
	}
	
	
	public void setNeedsRedraw(boolean deep) {
		mapper.setNeedsRedraw(deep);
	}

	


	
}
