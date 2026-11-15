package org.peakaboo.controller.mapper.dimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.accent.Pair;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.framework.plural.streams.StreamExecutor;

public class MapDimensionsController extends EventfulType<MapUpdateType>{

	/** Guessing gets slow on large maps, so we don't auto-guess for large maps */
	public static final int AUTO_GUESS_MAX_SCANS = 250_000;

	/** The most elongated shape, in either direction, that guessing will consider */
	private static final float MAX_GUESS_ASPECT = 50f;

	private MappingController mappingController;
	private Coord<Integer> viewDimensions = new Coord<>(1, 1);
	
	public MapDimensionsController(MappingController mappingController) {
		this.mappingController = mappingController;
		
		if (mappingController.rawDataController.getOriginalDataDimensions() != null) {
			viewDimensions = new Coord<>(mappingController.rawDataController.getOriginalDataWidth(), mappingController.rawDataController.getOriginalDataHeight());
		} else {
			viewDimensions = new Coord<>(mappingController.rawDataController.getMapResultSet().getMap(0).size(), 1);
		}
		
	}

	public Coord<Integer> getDimensions() {
		return new Coord<>(viewDimensions);
	}
	
	

	// data height and width
	public void setUserDataHeight(int height)
	{
		
		if (getUserDataWidth() * height > mappingController.rawDataController.getMapSize()) {
			height = mappingController.rawDataController.getMapSize() / getUserDataWidth();
		}
		if (height < 1) height = 1;

		viewDimensions.y = height;
				
		updateListeners(MapUpdateType.DATA_SIZE);
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

		if (getUserDataHeight() * width > mappingController.rawDataController.getMapSize()) {
			width = mappingController.rawDataController.getMapSize() / getUserDataHeight();
		}
		if (width < 1) width = 1;

		viewDimensions.x = width;
		
		updateListeners(MapUpdateType.DATA_SIZE);
	}

	
	/**
	 * Sets the height of the map as specified by the user
	 * @return
	 */
	public int getUserDataWidth()
	{
		return viewDimensions.x;
	}
	
	
	/** Sets both dimensions at once, clamped so that they fit within the data set */
	public void setUserDimensions(Coord<Integer> dimensions) {
		int size = mappingController.rawDataController.getMapSize();
		int width = Math.max(1, Math.min(dimensions.x, size));
		int height = Math.max(1, Math.min(dimensions.y, size / width));
		viewDimensions = new Coord<>(width, height);
		updateListeners(MapUpdateType.DATA_SIZE);
	}

	/**
	 * Returns true if the data set declares dimensions we can trust. Some sources
	 * with no height axis declare Nx1, so we only trust declared sizes when both
	 * axes are larger than 1.
	 */
	public boolean hasUsableDeclaredDimensions() {
		var raw = mappingController.rawDataController;
		return raw.hasOriginalDataDimensions() && raw.getOriginalDataWidth() > 1 && raw.getOriginalDataHeight() > 1;
	}

	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < viewDimensions.x && mapCoord.y >= 0 && mapCoord.y < viewDimensions.y);
	}
	
	
	

	public StreamExecutor<Coord<Integer>> guessDataDimensions() {
		
		//We don't need the real detector profile just to guess the dimensions
		//we also don't want filtered maps, since the size of the data may change with map resizing
		Spectrum all = mappingController.rawDataController.getMapResultSet().getSummedRawMap(new BasicDetectorProfile());
		
		// Limit aspect ratio to MAX_GUESS_ASPECT on eithe axis. Always leave at least 2
		// rows and 2 columns.
		int size = all.size();
		int min = (int) Math.max(Math.ceil(Math.sqrt(size / MAX_GUESS_ASPECT)), 2);
		int max = (int) Math.min(Math.floor(Math.sqrt(size * MAX_GUESS_ASPECT)), size / 2);
		List<Integer> widths = new ArrayList<>();
		for (int x = min; x <= max; x++) {
			widths.add(x);
		}
		
		StreamExecutor<Coord<Integer>> executor = new StreamExecutor<>("Evaluating Sizes");
		executor.setTask(widths, stream -> {
			
			long t1 = System.currentTimeMillis();
			
			Optional<Pair<Coord<Integer>, Float>> best = stream.map(x -> {

				// Whole rows only, the dimension setters won't accept sizes larger than the data.
				int y = all.size() / x;
				float delta = map2dDeltaSum(all, x, y);

				return new Pair<>(new Coord<>(x, y), delta);
				
			}).min((a, b) -> a.second.compareTo(b.second));
			
			
			long t2 = System.currentTimeMillis();
			OneLog.log(Level.INFO, "Guessed Data Dimensions in " + ((t2-t1)/1000) + " seconds");
			
			if (best.isPresent()) {
				return best.get().first;
			} else {
				return null;
			}
		});
		
		return executor;

		
	}
	
	
	

	
	// Calculates the average difference between vertically adjacent points. The
	// caller must ensure width * height fits within the map.
	private float map2dDeltaSum(Spectrum map, int width, int height) {
		float[] maparray = map.backingArray();
		float delta = 0;
		float count = 0;

		for (int y = 1; y < height; y++) {
			int rowind = y*width;

			for (int x = 0; x < width; x++) {
				int ind = rowind+x;
				delta += Math.abs(maparray[ind] - maparray[ind-width]);
				count++;
			}
		}
		
		if (count == 0) { return 0; }
		return delta/count;
		
	}
	
	
	
	
}
