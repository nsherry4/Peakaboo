package org.peakaboo.controller.mapper.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.Spectrum;
import eventful.EventfulType;
import plural.streams.StreamExecutor;

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
	public boolean	monochrome			= false;

	public float	zoom				= 1f;
	
	public Coord<Integer> viewDimensions = new Coord<Integer>(1, 1);
	
	public File		savePictureFolder 	= null;
	public File		dataSourceFolder 	= null;

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


	

	// data height and width
	public void setUserDataHeight(int height)
	{

		if (getUserDataWidth() * height > mapController.rawDataController.getMapSize()) height = mapController.rawDataController.getMapSize() / getUserDataWidth();
		if (height < 1) height = 1;

		viewDimensions.y = height;
				
		updateListeners(UpdateType.DATA_SIZE.toString());
	}


	/**
	 * Gets the height of the map as specified by the user
	 */
	public int getUserDataHeight()
	{
		return viewDimensions.y;
	}


	public void setUserDataWidth(int width)
	{

		if (getUserDataHeight() * width > mapController.rawDataController.getMapSize()) width = mapController.rawDataController.getMapSize() / getUserDataHeight();
		if (width < 1) width = 1;

		viewDimensions.x = width;
		
		updateListeners(UpdateType.DATA_SIZE.toString());
	}

	
	/**
	 * Sets the height of the map as specified by the user
	 * @return
	 */
	public int getUserDataWidth()
	{
		return viewDimensions.x;
	}
	
	
	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < viewDimensions.x && mapCoord.y >= 0 && mapCoord.y < viewDimensions.y);
	}
	
	
	

	public StreamExecutor<Coord<Integer>> guessDataDimensions() {
		
		//We don't need the real calibration profile just to guess the dimensions
		//we also don't want filtered maps, since the size of the data may change with map resizing
		Spectrum all = mapController.rawDataController.getMapResultSet().getSummedRawMap(new CalibrationProfile());
		
		//find the highest average edge delta
		int min = (int) Math.max(Math.sqrt(all.size()) / 15, 2); //don't consider dimensions that are too small
		List<Integer> widths = new ArrayList<>();
		for (int x = min; x <= all.size() / min; x++) {
			widths.add(x);
		}
		
		StreamExecutor<Coord<Integer>> executor = new StreamExecutor<>("Evaluating Sizes");
		executor.setTask(widths, stream -> {
			
			long t1 = System.currentTimeMillis();
			
			Optional<Pair<Coord<Integer>, Float>> best = stream.map(x -> {
				
				float delta;
				int y;
				if (all.size() % x == 0) {
					y = all.size() / x;
					delta = getDimensionScore(all, x, y);
				} else {
					y = (int)Math.ceil(all.size() / (float)x);
					delta = getDimensionScore(all, x, y); //include the last incomplete row
				}
				
				return new Pair<>(new Coord<>(x, y), delta);
				
			}).min((a, b) -> a.second.compareTo(b.second));
			
			
			long t2 = System.currentTimeMillis();
			PeakabooLog.get().log(Level.INFO, "Guessed Data Dimensions in " + ((t2-t1)/1000) + " seconds");
			
			if (best.isPresent()) {
				return best.get().first;
			} else {
				return null;
			}
		});
		
		return executor;

		
	}
	
	
	//helper for guessDataDimensions, calculates the deltas along the wrapping 
	//left-hand edge of a map between the end of one row and the start of the 
	//next. Higher values should indicate the dimensions are correct and the 
	//two sets of points are not next to each other.
	private float getDimensionScore(Spectrum map, int width, int height) {
		return map2dDelta(map2dDelta(map, width, height), width, height).sum();
	}
	
	private Spectrum map2dDelta(Spectrum map, int width, int height) {
		float[] maparray = map.backingArray();
		Spectrum deltas = new ISpectrum(map.size());
		float[] deltasarray = deltas.backingArray();
		float delta = 0;
		float value = 0;
		float count = 0;
		int mapsize = map.size();
		
		// calculate offsets for indexes of neighbouring pixels. these can be used to
		// get the neighbours for that pixel faster than doing the math at every point
		int noff = -width;
		int eoff = 1;
		int soff = width;
		int woff = -1;
		
		for (int y = 0; y < height; y++) {
			int rowind = y*width;
			
			for (int x = 0; x < width; x++) {
				int ind = rowind+x;
				if (ind >= mapsize) break;
				value = maparray[ind];
				count = 0;
				delta = 0;
				
				//west
				if (x > 0) {
					delta += Math.abs(value - maparray[ind+woff]);
					count++;
				}
				
				//north
				if (y > 0) {
					delta += Math.abs(value - maparray[ind+noff]);
					count++;
				}

				//east
				if (x < width-1 && ind+eoff < mapsize) {
					delta += Math.abs(value - maparray[ind+eoff]);
					count++;
				}
				
				//south
				if (y < height-1 && ind+soff < mapsize) {
					delta += Math.abs(value - maparray[ind+soff]);
					count++;
				}
				
				deltasarray[ind] = delta/count;

			}
		}
		
		return deltas;
		
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
			return new Coord<Number>( realDims.x.end, 		realDims.y.end);
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
			return new Coord<Number>( realDims.x.end,		realDims.y.start);
		} else {
			return new Coord<Number>(mapController.getFiltering().getFilteredDataWidth(), 1);
		}
	}







	

	
	
}