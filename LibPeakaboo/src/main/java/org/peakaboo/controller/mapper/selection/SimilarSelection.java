package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.display.map.modes.composite.CompositeModeData;
import org.peakaboo.display.map.modes.correlation.CorrelationModeData;
import org.peakaboo.display.map.modes.ratio.RatioModeData;
import org.peakaboo.display.map.modes.ternary.TernaryModeData;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * Represents a selection of similar points, which may not be contiguous 
 * @author NAS
 *
 */

class SimilarSelection extends AbstractSelection {

	private List<Integer> indexes = new ArrayList<>();
	
	
	private Parameter<Float> threshold;
	private Parameter<Integer> padding;
	private Group parameters;
	
	public SimilarSelection(MappingController map) {
		super(map);
		threshold = new Parameter<>("Threshold", new RealSpinnerStyle(), 1.2f, t -> t.getValue() >= 1f && t.getValue() <= 100f);
		padding = new Parameter<>("Padding", new IntegerSpinnerStyle(), 0, t -> t.getValue() >= 0 && t.getValue() <= 10);
		parameters = new Group("Settings", threshold, padding);
	}


	public List<Integer> selectPoint(Coord<Integer> clickedAt, boolean contiguous) {
		indexes.clear();
		
		MapModes displayMode = map.getFitting().getMapDisplayMode();
		Pair<Spectrum, List<Integer>> displayModeData = getDisplayModeData();
		Spectrum data = displayModeData.first;
		List<Integer> invalid = displayModeData.second;
		
		
		Coord<Integer> mapSize = mapSize();
		int w = mapSize.x;
		int h = mapSize.y;
		GridPerspective<Float> grid = new GridPerspective<>(w, h, null);
		float value = grid.get(data, clickedAt.x, clickedAt.y);
		
		//If we're selecting on a ratio map, and the selected point is 1:10 instead of 10:1,
		//it will be represented as a negative number. We flip it here for convenience
		if (displayMode == MapModes.RATIO && value < 0f) {
			for (int i = 0; i < data.size(); i++) {
				data.set(i, -data.get(i));
			}
			value = grid.get(data, clickedAt.x, clickedAt.y);
		}

		
		List<Integer> points;
		if (! contiguous) {
			points = selectNonContiguous(data, invalid, value, grid);
		} else {
			points = selectContiguous(data, invalid, clickedAt, grid);
		}
		
		int paddingValue = padding.getValue();
		for (int i = 0; i < paddingValue; i++) {
			points = padSelection(points);
		}
		
		
		indexes = points;
		
		return indexes;
		
	}
	

	/**
	 * Returns a Spectrum representing scalar data for the map based on the current map display mode. Also returns a list of unselectable points
	 * @return
	 */
	private Pair<Spectrum, List<Integer>> getDisplayModeData() {
		MapModes displayMode = map.getFitting().getMapDisplayMode();
		Spectrum data = null;
		List<Integer> unselectable = new ArrayList<>();
		
		switch(displayMode) {
		case COMPOSITE:
			CompositeModeData compositeData = (CompositeModeData) map.getFitting().getMapModeData();
			data = compositeData.getData();
			break;
		
		case CORRELATION:
			CorrelationModeData correlationData = (CorrelationModeData) map.getFitting().getMapModeData();
			data = correlationData.data;
			break;

		case RATIO:
			RatioModeData ratiodata = (RatioModeData) map.getFitting().getMapModeData();
			//we modity ratio data, so we make it a copy
			data = new ISpectrum(ratiodata.getData().first);
			Spectrum invalidMap = ratiodata.getData().second;
			for (int i = 0; i < invalidMap.size(); i++) {
				if (invalidMap.get(i) > 0f) {
					unselectable.add(i);
				}
			}
			break;
			
		case TERNARYPLOT:
			TernaryModeData ternaryData = (TernaryModeData) map.getFitting().getMapModeData();
			data = ternaryData.data;
			unselectable = ternaryData.unselectables;
			break;
			
		case OVERLAY:
		default:
			throw new UnsupportedOperationException("Cannot perform similarity-based selection on this map mode");
		}
		
		return new Pair<Spectrum, List<Integer>>(data, unselectable);
	}
	
	private List<Integer> selectContiguous(Spectrum data, 
			List<Integer> invalid,
			Coord<Integer> clickedAt,
			GridPerspective<Float> grid
		) {
		

		List<Integer> points = new ArrayList<>();
		//hashsets are faster for `contains` operations, which is what these will be heavily used for
		Set<Integer> pointSet = new HashSet<>();
		Set<Integer> invalidPoints = new HashSet<>(invalid);
		float value = grid.get(data, clickedAt.x, clickedAt.y);
		float thresholdValue = threshold.getValue();
		int point = grid.getIndexFromXY(clickedAt.x, clickedAt.y);
		points.add(point);
		pointSet.add(point);
		int cursor = 0;
		while (cursor < points.size()) {
			point = points.get(cursor);
			int x, y;
			
			int[] neighbours = new int[] {grid.north(point), grid.south(point), grid.east(point), grid.west(point)};
			for (int neighbour : neighbours) {
				//out-of-bounds, re-tread, invalid point checks
				if (neighbour == -1) { continue; }
				if (pointSet.contains(neighbour)) { continue; }
				if (invalidPoints.contains(neighbour)) { continue; }
				
				x = grid.getXYFromIndex(neighbour).first;
				y = grid.getXYFromIndex(neighbour).second;
				
				float other = grid.get(data, x, y);
				// match * or / threshold percent (eg threshold=1.2 so (other/1.2, other*1.2) 
				float otherMin = other / thresholdValue;
				float otherMax = other * thresholdValue;
				if (value >= otherMin && value <= otherMax) {
					points.add(neighbour);
					pointSet.add(neighbour);
				}
			}

			cursor++;
		}
		
		return points;
	}


	private List<Integer> selectNonContiguous(Spectrum data, List<Integer> invalid, float value, GridPerspective<Float> grid) {
		//All points, even those not touching
		List<Integer> points = new ArrayList<>();
		float thresholdValue = threshold.getValue();
		for (int y : new Range(0, grid.height-1)) {
			for (int x : new Range(0, grid.width-1)) {
				float other = grid.get(data, x, y);
				// match +/- threshold percent
				float otherMin = other / thresholdValue;
				float otherMax = other * thresholdValue;
				if (value >= otherMin && value <= otherMax) {
					points.add(grid.getIndexFromXY(x, y));
				}
			}
		}
		points.removeAll(invalid);
		return points;
	}
	
	private List<Integer> padSelection(List<Integer> points) {
		Set<Integer> pointSet = new HashSet<>();
		pointSet.addAll(points);
	
		Coord<Integer> mapSize = mapSize();
		int w = mapSize.x;
		int h = mapSize.y;
		GridPerspective<Float> grid = new GridPerspective<>(w, h, null);
		
		//visit all existing points
		for (Integer point : points) {
			
			int[] neighbours = new int[] {grid.north(point), grid.south(point), grid.east(point), grid.west(point)};
			for (int neighbour : neighbours) {
				//out-of-bounds, re-tread checks
				if (neighbour == -1) continue;
				if (pointSet.contains(neighbour)) continue;

				pointSet.add(neighbour);
				
			}
		}
		
		return new ArrayList<>(pointSet);
		
		
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.of(parameters);
	}

	@Override
	public List<Integer> startDragSelection(Coord<Integer> point) {
		return Collections.emptyList();
	}

	@Override
	public List<Integer> addDragSelection(Coord<Integer> point) {
		return Collections.emptyList();
	}

	@Override
	public List<Integer> releaseDragSelection(Coord<Integer> point) {
		return Collections.emptyList();
	}


	
}
