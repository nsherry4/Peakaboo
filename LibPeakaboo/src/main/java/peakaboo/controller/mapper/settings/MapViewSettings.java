package peakaboo.controller.mapper.settings;

import java.io.File;

import cyclops.Bounds;
import cyclops.Coord;
import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;

/**
 * Settings for a map view which are not directly dependent on the contents 
 * of the map (eg no TransisionSeries lists, etc)
 * @author NAS
 *
 */
public class MapViewSettings extends EventfulType<String> //TODO remove extends
{

	//SOURCE DATA
	private MappingController mapController;
	
	
	//SETTINGS
	public boolean	drawCoordinates		= true;
	public boolean	drawSpectrum		= true;
	public boolean	drawTitle			= true;
	private boolean drawScaleBar		= true;
	
	public boolean	showDataSetTitle	= false;
	public int		spectrumSteps		= 15;
	public boolean	contour				= false;
	public int		interpolation		= 0;
	public boolean	monochrome			= false;

	public float	zoom				= 1f;
	
	public Coord<Integer> viewDimensions = new Coord<Integer>(1, 1);
	
	public File		savePictureFolder 	= null;
	public File		dataSourceFolder 	= null;

	//place (0, 0) in the top left corner rather than bottom left corner
	private boolean screenOrientation = false;


	private float overlayLowCutoff		= 0f;


	
	
	
	public MapViewSettings(MappingController mapController, MapViewSettings copy)
	{
		this(mapController);
		
		//Any settings which should persist between map windows should be copied here
		if (copy != null) {
			this.drawCoordinates = copy.drawCoordinates;
			this.drawSpectrum = copy.drawSpectrum;
			this.drawTitle = copy.drawTitle;
			this.showDataSetTitle = copy.showDataSetTitle;
			this.spectrumSteps = copy.spectrumSteps;
			this.contour = copy.contour;
			this.interpolation = copy.interpolation;
			this.monochrome = copy.monochrome;
			
			this.viewDimensions = new Coord<>(copy.viewDimensions);
			
			this.savePictureFolder = copy.savePictureFolder;
			this.dataSourceFolder = copy.dataSourceFolder;
			
			this.zoom = copy.zoom;
		}
	}
	
	
	public MapViewSettings(MappingController mapController)
	{
		setMappingController(mapController);
	}
	
	
	public void setMappingController(MappingController controller) {
		this.mapController = controller;
		if (mapController.rawDataController.getOriginalDataDimensions() != null) {
			viewDimensions = new Coord<>(mapController.rawDataController.getOriginalDataWidth(), mapController.rawDataController.getOriginalDataHeight());
		} else {
			viewDimensions = new Coord<Integer>(mapController.rawDataController.getMapResultSet().getMap(0).size(), 1);
		}
	}


	
	// interpolation
	public void setInterpolation(int passes)
	{
		int side, newside;
		while (true) {
			
			side = (int)Math.sqrt( getDataHeight() * getDataWidth() );
			
			newside = (int)(side * Math.pow(2, passes));
		
			if (newside > 750) {
				passes--;
			} else {
				break;
			}
		
		}

		//Hard cap to prevent wildly making up data that doesn't exist
		passes = Math.min(passes, 3);
		
		if (passes < 0) passes = 0;
		interpolation = passes;
		updateListeners(UpdateType.DATA_OPTIONS.toString());
		
		
		//Interpolation is incompatible with points selection
		if (passes > 0 && mapController.getSettings().getPointsSelection().hasSelection()) {
			mapController.getSettings().getPointsSelection().clearSelection();
		}
		
	}
	
	public int getInterpolation()
	{
		return interpolation;
	}
	
	

	// data height and width
	public void setDataHeight(int height)
	{

		if (getDataWidth() * height > mapController.rawDataController.getMapSize()) height = mapController.rawDataController.getMapSize() / getDataWidth();
		if (height < 1) height = 1;

		viewDimensions.y = height;
		
		setInterpolation(interpolation);
		
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}


	public int getDataHeight()
	{
		return viewDimensions.y;
	}


	public void setDataWidth(int width)
	{

		if (getDataHeight() * width > mapController.rawDataController.getMapSize()) width = mapController.rawDataController.getMapSize() / getDataHeight();
		if (width < 1) width = 1;

		viewDimensions.x = width;
		
		setInterpolation(interpolation);
		
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}

	public int getDataWidth()
	{
		return viewDimensions.x;
	}
	
	
	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < viewDimensions.x && mapCoord.y >= 0 && mapCoord.y < viewDimensions.y);
	}
	

	// contours
	public void setContours(boolean contours)
	{
		this.contour = contours;
				
		updateListeners(UpdateType.UI_OPTIONS.toString());
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
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}
	

	// spectrum
	public void setSpectrumSteps(int steps)
	{
		if (steps > 25) steps = 25;
		if (steps > 0)
		{
			this.spectrumSteps = steps;
		}
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public int getSpectrumSteps()
	{
		return this.spectrumSteps;
	}


	public void setMonochrome(boolean mono)
	{
		this.monochrome = mono;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getMonochrome()
	{
		return this.monochrome;
	}

	

	public void setShowSpectrum(boolean show)
	{
		this.drawSpectrum = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowSpectrum()
	{
		return this.drawSpectrum;
	}


	public void setShowScaleBar(boolean show) {
		this.drawScaleBar = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}
	
	public boolean getShowScaleBar() {
		return this.drawScaleBar;
	}
	
	public void setShowTitle(boolean show)
	{
		this.drawTitle = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowTitle()
	{
		return this.drawTitle;
	}


	public void setShowDatasetTitle(boolean show)
	{
		this.showDataSetTitle = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowDatasetTitle()
	{
		return this.showDataSetTitle;
	}


	public void setShowCoords(boolean show)
	{
		this.drawCoordinates = show;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public boolean getShowCoords()
	{
		return this.drawCoordinates;
	}
	
	
	public boolean getDrawCoords()
	{
		return this.drawCoordinates;
	}
	public void setDrawCoords(boolean draw)
	{
		this.drawCoordinates = draw;
	}
	
	
	public boolean getScreenOrientation() {
		return this.screenOrientation;
	}

	public void setScreenOrientation(boolean screenOrientation) {
		this.screenOrientation = screenOrientation;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}


	public float getOverlayLowCutoff() {
		return this.overlayLowCutoff;
	}

	public void setOverlayLowCutoff(float cutoff) {
		if (cutoff == this.overlayLowCutoff) {
			return;
		}
		this.overlayLowCutoff = cutoff;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}
	

	public int getInterpolatedHeight()
	{
		
		int height = getDataHeight();
		
		for (int i = 0; i < getInterpolation(); i++)
		{
			height = height * 2 - 1;
		}
		
		return height;
		
	}


	public int getInterpolatedWidth()
	{
		int width = getDataWidth();
		
		for (int i = 0; i < getInterpolation(); i++)
		{
			width = width * 2 - 1;
		}
		
		return width;
		
	}
	
	

	public Coord<Number> getLoXLoYCoord() {
		Coord<Bounds<Number>> realDims = mapController.rawDataController.getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>(realDims.x.start, realDims.y.end);
		} else {
			return new Coord<Number>(1, mapController.getSettings().getView().getDataHeight());
		}
	}
	public Coord<Number> getHiXLoYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.rawDataController.getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.end, 		realDims.y.end);
		} else {
			return new Coord<Number>(mapController.getSettings().getView().getDataWidth(), mapController.getSettings().getView().getDataHeight());
		}
	}
	public Coord<Number> getLoXHiYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.rawDataController.getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.start,	realDims.y.start);
		} else {
			return new Coord<Number>(1, 1);
		}
	}
	public Coord<Number> getHiXHiYCoord()
	{
		Coord<Bounds<Number>> realDims = mapController.rawDataController.getRealDimensions();
		if (realDims != null) {
			return new Coord<Number>( realDims.x.end,		realDims.y.start);
		} else {
			return new Coord<Number>(mapController.getSettings().getView().getDataWidth(), 1);
		}
	}







	

	
	
}