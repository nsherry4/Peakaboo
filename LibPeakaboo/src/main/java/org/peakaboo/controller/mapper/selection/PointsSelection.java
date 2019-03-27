package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.display.map.modes.composite.CompositeModeData;
import org.peakaboo.display.map.modes.ratio.RatioModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.eventful.EventfulType;

/**
 * Represents a selection of points, which may not be contiguous 
 * @author NAS
 *
 */

public class PointsSelection extends EventfulType<MapUpdateType>{

	private List<Integer> indexes = new ArrayList<>();
	private MappingController map;
	
	
	private float threshold = 1.2f;
	private int padding = 0;
	
	public PointsSelection(MappingController map) {
		this.map = map;
	}
	
	public boolean hasSelection() {
		
		// TODO: this check can be moved further down the line later. There's no reason
		// why we can't make any selections just because that selection is not
		// replottable
		return indexes.size() > 0 && map.getFiltering().isReplottable() && map.getFitting().getMapModeData().isReplottable();
	}
	
	public void clearSelection() {
		setPoints(new ArrayList<>());
	}
	
	public List<Integer> getPoints() {
		return indexes;
	}

	public void setPoints(List<Integer> indexes) {
		this.indexes = indexes;
		updateListeners(MapUpdateType.POINT_SELECTION);
	}
	

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
		updateListeners(MapUpdateType.POINT_SELECTION);
	}
	
	

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		updateListeners(MapUpdateType.POINT_SELECTION);
	}

	public void makeSelection(Coord<Integer> clickedAt, boolean contiguous, boolean modify) {
		
		MapModes displayMode = map.getFitting().getMapDisplayMode();
		Spectrum data = null;
		List<Integer> invalid = new ArrayList<>();
		

		
		if (displayMode == MapModes.COMPOSITE) {
			CompositeModeData compositeData = (CompositeModeData) map.getFitting().getMapModeData();
			data = compositeData.getData();
		} else if (displayMode == MapModes.RATIO) {
			RatioModeData ratiodata = (RatioModeData) map.getFitting().getMapModeData();
			data = ratiodata.getData().first;
			Spectrum invalidMap = ratiodata.getData().second;
			for (int i = 0; i < invalidMap.size(); i++) {
				if (invalidMap.get(i) > 0f) {
					invalid.add(i);
				}
			}
		}
		
		int w = map.getUserDimensions().getUserDataWidth();
		int h = map.getUserDimensions().getUserDataHeight();
		GridPerspective<Float> grid = new GridPerspective<Float>(w, h, null);
		int clickedAtIndex = grid.getIndexFromXY(clickedAt.x, clickedAt.y);
		float value = grid.get(data, clickedAt.x, clickedAt.y);
		
		//If we're selecting on a ratio map, and the selected point is 1:10 instead of 10:1,
		//it will be represented as a negative number. We flip it here for convenience
		if (displayMode == MapModes.RATIO && value < 0f) {
			for (int i = 0; i < data.size(); i++) {
				data.set(i, -data.get(i));
			}
			value = grid.get(data, clickedAt.x, clickedAt.y);
		}
		
		
		List<Integer> points = new ArrayList<>();
		if (! contiguous) {
			//All points, even those not touching
			for (int y : new Range(0, h-1)) {
				for (int x : new Range(0, w-1)) {
					float other = grid.get(data, x, y);
					// match +/- threshold percent
					float otherMin = other / threshold;
					float otherMax = other * threshold;
					if (value >= otherMin && value <= otherMax) {
						points.add(grid.getIndexFromXY(x, y));
					}
				}
			}
		} else {
			Set<Integer> pointSet = new HashSet<>();
			int point = grid.getIndexFromXY(clickedAt.x, clickedAt.y);
			points.add(point);
			pointSet.add(point);
			int cursor = 0;
			while (cursor < points.size()) {
				point = points.get(cursor);
				int x, y;
				
				int[] neighbours = new int[] {grid.north(point), grid.south(point), grid.east(point), grid.west(point)};
				for (int neighbour : neighbours) {
					//out-of-bounds, re-tread checks
					if (neighbour == -1) continue;
					if (pointSet.contains(neighbour)) continue;
					
					x = grid.getXYFromIndex(neighbour).first;
					y = grid.getXYFromIndex(neighbour).second;
					
					float other = grid.get(data, x, y);
					// match * or / threshold percent (eg threshold=1.2 so (other/1.2, other*1.2) 
					float otherMin = other / threshold;
					float otherMax = other * threshold;
					if (value >= otherMin && value <= otherMax) {
						points.add(neighbour);
						pointSet.add(neighbour);
					}
				}

				cursor++;
			}
			
			
		}
		
		for (int i = 0; i < padding; i++) {
			points = padSelection(points);
		}
		
		
		
		if (modify && getPoints().contains(clickedAtIndex))	{
			//if we're in modify selection mode, and the user clicked on an already 
			//selected point, then we remove these points from the previous selection
			List<Integer> merged = new ArrayList<>(getPoints());
			merged.removeAll(points);
			setPoints(merged);
		} else if (modify) {
			//if we're in modify selection mode and the user clicked on a point not
			//already selected, then we add these points to the previous selection
			//use a set to ensure uniqueness
			Set<Integer> merged = new HashSet<>(getPoints());
			merged.addAll(points);
			setPoints(new ArrayList<>(merged));
		} else {
			//we're not in modify mode, so we just set the selection to the current value
			setPoints(points);	
		}
		
		
	}
	
	private List<Integer> padSelection(List<Integer> points) {
		Set<Integer> pointSet = new HashSet<>();
		pointSet.addAll(points);
	
		int w = map.getUserDimensions().getUserDataWidth();
		int h = map.getUserDimensions().getUserDataHeight();
		GridPerspective<Float> grid = new GridPerspective<Float>(w, h, null);
		
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
	
}
