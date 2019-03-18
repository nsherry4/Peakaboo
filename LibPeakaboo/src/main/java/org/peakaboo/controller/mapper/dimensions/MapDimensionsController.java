package org.peakaboo.controller.mapper.dimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;

import cyclops.Coord;
import cyclops.Pair;
import cyclops.Spectrum;
import eventful.EventfulType;
import plural.streams.StreamExecutor;

public class MapDimensionsController extends EventfulType<String>{

	private MappingController mappingController;
	private Coord<Integer> viewDimensions = new Coord<Integer>(1, 1);
	
	public MapDimensionsController(MappingController mappingController) {
		this.mappingController = mappingController;
		
		if (mappingController.rawDataController.getOriginalDataDimensions() != null) {
			viewDimensions = new Coord<>(mappingController.rawDataController.getOriginalDataWidth(), mappingController.rawDataController.getOriginalDataHeight());
		} else {
			viewDimensions = new Coord<Integer>(mappingController.rawDataController.getMapResultSet().getMap(0).size(), 1);
		}
		
	}

	public Coord<Integer> getDimensions() {
		return new Coord<>(viewDimensions);
	}
	
	

	// data height and width
	public void setUserDataHeight(int height)
	{

		if (getUserDataWidth() * height > mappingController.rawDataController.getMapSize()) height = mappingController.rawDataController.getMapSize() / getUserDataWidth();
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

		if (getUserDataHeight() * width > mappingController.rawDataController.getMapSize()) width = mappingController.rawDataController.getMapSize() / getUserDataHeight();
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
		Spectrum all = mappingController.rawDataController.getMapResultSet().getSummedRawMap(new CalibrationProfile());
		
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
					delta = map2dDeltaSum(all, x, y);
				} else {
					y = (int)Math.ceil(all.size() / (float)x);
					delta = map2dDeltaSum(all, x, y); //include the last incomplete row
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
	
	
	

	
	//Calculates the delta of the map
	private float map2dDeltaSum(Spectrum map, int width, int height) {
		float[] maparray = map.backingArray();
		float delta = 0;
		float count = 0;
		int mapsize = map.size();

		for (int y = 1; y < height; y++) {
			int rowind = y*width;
			
			for (int x = 0; x < width; x++) {
				int ind = rowind+x;
				if (ind >= mapsize) break;
				
				delta += Math.abs(maparray[ind] - maparray[ind-width]);
				count++;
			}
		}
		
		return delta/count;
		
	}
	
	
	
	
}
