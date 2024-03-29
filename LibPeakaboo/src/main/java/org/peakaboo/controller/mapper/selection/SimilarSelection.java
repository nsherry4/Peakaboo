package org.peakaboo.controller.mapper.selection;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Represents a selection of similar points, which may not be contiguous 
 * @author NAS
 *
 */

class SimilarSelection extends AbstractSelection {

	private IntArrayList indexes = new IntArrayList();
	
	
	private Parameter<Float> threshold;
	private Parameter<Integer> padding;
	private Group parameters;
	
	public SimilarSelection(MappingController map) {
		super(map);
		threshold = new Parameter<>("Threshold", new RealSpinnerStyle(), 1.2f, t -> t.getValue() >= 1f && t.getValue() <= 100f);
		padding = new Parameter<>("Padding", new IntegerSpinnerStyle(), 0, t -> t.getValue() >= 0 && t.getValue() <= 10);
		parameters = new Group("Settings", threshold, padding);
	}


	public IntArrayList selectPoint(Coord<Integer> clickedAt, boolean contiguous) {
		indexes.clear();
		
		var selectionInfo = map.getFitting().getMapModeData().getMapSelectionInfo().orElseGet(() -> null);
		if (selectionInfo == null) {
			return new IntArrayList();
		}		
		Spectrum data = selectionInfo.map();
		IntArrayList invalid = selectionInfo.unselectable();
		
		
		Coord<Integer> mapSize = mapSize();
		int w = mapSize.x;
		int h = mapSize.y;
		GridPerspective<Float> grid = new GridPerspective<>(w, h, null);
		float value = grid.get(data, clickedAt.x, clickedAt.y);
				
		IntArrayList points;
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
	
	
	private IntArrayList selectContiguous(Spectrum data, 
			IntArrayList invalid,
			Coord<Integer> clickedAt,
			GridPerspective<Float> grid
		) {
		

		IntArrayList points = new IntArrayList();
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
			point = points.getInt(cursor);
			int x, y;
			
			int[] neighbours = new int[] {grid.north(point), grid.south(point), grid.east(point), grid.west(point)};
			for (int neighbour : neighbours) {
				//out-of-bounds, re-tread, invalid point checks
				if (neighbour == -1) { continue; }
				if (pointSet.contains(neighbour)) { continue; }
				if (invalidPoints.contains(neighbour)) { continue; }
				
				var xy = grid.getXYFromIndex(neighbour);
				x = xy.first;
				y = xy.second;
				
				float other = grid.get(data, x, y);
				// match * or / threshold percent (eg threshold=1.2 so (other/1.2, other*1.2)
				// for negative values, we reverse min and max since 8>5, but -8 !> -5
				float otherMin, otherMax;
				if (other > 0) {
					otherMin = other / thresholdValue;
					otherMax = other * thresholdValue;	
				} else {
					otherMin = other * thresholdValue;
					otherMax = other / thresholdValue;	
				}
				if (value >= otherMin && value <= otherMax) {
					points.add(neighbour);
					pointSet.add(neighbour);
				}
			}

			cursor++;
		}
		
		return points;
	}


	private IntArrayList selectNonContiguous(Spectrum data, IntArrayList invalid, float value, GridPerspective<Float> grid) {
		//All points, even those not touching
		IntArrayList points = new IntArrayList();
		float thresholdValue = threshold.getValue();
		for (int y : new Range(0, grid.height)) {
			for (int x : new Range(0, grid.width)) {
				float other = grid.get(data, x, y);
				
				// match * or / threshold percent (eg threshold=1.2 so (other/1.2, other*1.2)
				// for negative values, we reverse min and max since 8>5, but -8 !> -5
				float otherMin, otherMax;
				if (other > 0) {
					otherMin = other / thresholdValue;
					otherMax = other * thresholdValue;
				} else {
					otherMin = other * thresholdValue;
					otherMax = other / thresholdValue;
				}
				//If it's in bounds, add the point
				if (value >= otherMin && value <= otherMax) {
					points.add(grid.getIndexFromXY(x, y));
				}
			}
		}
		points.removeAll(invalid);
		return points;
	}
	
	private IntArrayList padSelection(IntArrayList points) {
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
		
		return new IntArrayList(pointSet);
		
		
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.of(parameters);
	}

	@Override
	public IntArrayList startDragSelection(Coord<Integer> point) {
		return IntArrayList.of();
	}

	@Override
	public IntArrayList addDragSelection(Coord<Integer> point) {
		return IntArrayList.of();
	}

	@Override
	public IntArrayList releaseDragSelection(Coord<Integer> point) {
		return IntArrayList.of();
	}


	
}
