package peakaboo.controller.mapper.data;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.SISize;
import cyclops.Spectrum;
import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.mapping.calibration.CalibrationProfile;
import peakaboo.mapping.results.MapResultSet;
import plural.streams.StreamExecutor;


public class MapSetController extends EventfulType<String>
{

	MapSetMapData mapModel;
	
	
	public MapSetController()
	{
		mapModel = new MapSetMapData();
	}
	


	/**
	 * Sets the map's data model. dataDimensions, realDimensions, realDimensionsUnits may be null
	 * @param calibrationProfile 
	 */
	public void setMapData(
			MapResultSet data,
			String datasetName,
			List<Integer> badPoints,
			Coord<Integer> dataDimensions,
			Coord<Bounds<Number>> realDimensions,
			SISize realDimensionsUnits, 
			CalibrationProfile calibrationProfile
			
	)
	{
	
		
		mapModel.mapResults = data;
		mapModel.datasetTitle = datasetName;
		mapModel.badPoints = badPoints;
		
		mapModel.originalDimensions = dataDimensions;
		mapModel.originalDimensionsProvided = dataDimensions != null;
		mapModel.realDimensions = realDimensions;
		mapModel.realDimensionsUnits = realDimensionsUnits;
		mapModel.calibrationProfile = calibrationProfile;

		updateListeners(UpdateType.DATA.toString());

	}
	
	public int getMapSize()
	{
		return mapModel.mapSize();
	}

	
	


	

	

	
	public Coord<Integer> getOriginalDataDimensions() {
		return mapModel.originalDimensions;
	}
	
	public boolean hasOriginalDataDimensions() {
		return mapModel.originalDimensionsProvided;
	}
	
	public int getOriginalDataHeight() {
		return mapModel.originalDimensions.y;
	}
	public int getOriginalDataWidth() {
		return mapModel.originalDimensions.x;
	}
	
	
	public StreamExecutor<Coord<Integer>> guessDataDimensions() {
		//We don't need the real calibration profile just to guess the dimensions
		Spectrum all = mapModel.mapResults.sumAllTransitionSeriesMaps(new CalibrationProfile());

		
		//find the highest average edge delta
		int min = (int) Math.max(Math.sqrt(all.size()) / 15, 2); //don't consider dimensions that are too small
		List<Integer> widths = new ArrayList<>();
		for (int x = min; x <= all.size() / min; x++) {
			widths.add(x);
		}
		
		StreamExecutor<Coord<Integer>> executor = new StreamExecutor<>("Evaluating Sizes");
		executor.setTask(widths, stream -> {
			
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
		Spectrum deltas = new ISpectrum(map.size());
		float delta = 0;
		float value = 0;
		int count = 0;
		int dind;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y*width+x >= map.size()) break;
				value = map.get(y*width+x);
				count = 0;
				delta = 0;
				
				dind = y*width+(x-1);
				if (x > 0) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				dind = (y-1)*width+x;
				if (y > 0) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}

				dind = y*width+(x+1);
				if (x < width-1 && dind < map.size()) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				dind = (y+1)*width+x;
				if (y < height-1 && dind < map.size()) {
					delta += Math.abs(value - map.get(dind));
					count++;
				}
				
				delta /= (float)count;
				deltas.set(y*width+x, delta);

			}
		}
		
		return deltas;
		
	}
	
	



	public List<Integer> getBadPoints()
	{
		return new ArrayList<>(mapModel.badPoints);
	}


	public String getDatasetTitle()
	{
		return mapModel.datasetTitle;
	}


	public void setDatasetTitle(String name)
	{
		mapModel.datasetTitle = name;
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		return mapModel.realDimensions;
	}
	
	
	public SISize getRealDimensionUnits()
	{
		return mapModel.realDimensionsUnits;
	}
	

	public MapResultSet getMapResultSet()
	{
		return mapModel.mapResults;
	}

	public CalibrationProfile getCalibrationProfile() {
		return mapModel.calibrationProfile;
	}
		
}
