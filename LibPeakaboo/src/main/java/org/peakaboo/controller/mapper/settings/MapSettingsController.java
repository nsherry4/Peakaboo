package org.peakaboo.controller.mapper.settings;

import java.io.File;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.palette.Gradient;
import org.peakaboo.framework.cyclops.visualization.palette.Gradients;
import org.peakaboo.framework.eventful.EventfulType;

/**
 * Settings for a map view which relate to presentation settings unrelated to
 * the maps themselves.
 * 
 * @author NAS
 */
public class MapSettingsController extends EventfulType<MapUpdateType>
{

	//SOURCE DATA
	private MappingController mapController;
	
	
	//SETTINGS
	private boolean	drawCoordinates		= true;
	private boolean	drawSpectrum		= true;
	private boolean	drawTitle			= true;
	private boolean drawScaleBar		= true;
	private boolean	drawDataSetTitle	= false;
	
	private int		spectrumSteps		= 15;
	private boolean	contour				= false;
	private Gradient colourGradient = Gradients.DEFAULT;

	private float	zoom				= 1f;

	public File		lastFolder 			= null;
	
		
	public MapSettingsController(MappingController mapController)
	{
		setMappingController(mapController);
		lastFolder = mapController.getParentPlotController().io().getLastFolder();
	}
	
	
	public void setMappingController(MappingController controller) {
		this.mapController = controller;
	}


	// contours
	public void setContours(boolean contours)
	{
		this.contour = contours;
				
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public boolean getContours()
	{
		return this.contour;
	}

	
	//zoom
	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}
	

	// spectrum
	public void setSpectrumSteps(int steps)
	{
		if (steps > 25) steps = 25;
		if (steps > 0)
		{
			this.spectrumSteps = steps;
		}
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public int getSpectrumSteps()
	{
		return this.spectrumSteps;
	}

	
	public void setColourGradient(Gradient gradient)
	{
		this.colourGradient = gradient;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public Gradient getColourGradient()
	{
		return this.colourGradient;
	}


	public void setShowSpectrum(boolean show)
	{
		this.drawSpectrum = show;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public boolean getShowSpectrum()
	{
		return this.drawSpectrum;
	}


	public void setShowScaleBar(boolean show) {
		this.drawScaleBar = show;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}
	
	public boolean getShowScaleBar() {
		return this.drawScaleBar;
	}
	
	public void setShowTitle(boolean show)
	{
		this.drawTitle = show;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public boolean getShowTitle()
	{
		return this.drawTitle;
	}


	public void setShowDatasetTitle(boolean show)
	{
		this.drawDataSetTitle = show;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public boolean getShowDatasetTitle()
	{
		return this.drawDataSetTitle;
	}


	public void setShowCoords(boolean show)
	{
		this.drawCoordinates = show;
		updateListeners(MapUpdateType.UI_OPTIONS);
	}


	public boolean getShowCoords()
	{
		return this.drawCoordinates;
	}
	

	public Coord<Number> getLoXLoYCoord() {
		Coord<Bounds<Number>> realDims = mapController.getFiltering().getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>(realDims.x.start, realDims.y.end);
		} else {
			return new Coord<Number>(1, mapController.getFiltering().getFilteredDataHeight());
		}
	}
	public Coord<Number> getHiXLoYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.getFiltering().getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.end, realDims.y.end);
		} else {
			return new Coord<Number>(mapController.getFiltering().getFilteredDataWidth(), mapController.getFiltering().getFilteredDataHeight());
		}
	}
	public Coord<Number> getLoXHiYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.getFiltering().getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.start,	realDims.y.start);
		} else {
			return new Coord<Number>(1, 1);
		}
	}
	public Coord<Number> getHiXHiYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.getFiltering().getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.end, realDims.y.start);
		} else {
			return new Coord<Number>(mapController.getFiltering().getFilteredDataWidth(), 1);
		}
	}







	

	
	
}