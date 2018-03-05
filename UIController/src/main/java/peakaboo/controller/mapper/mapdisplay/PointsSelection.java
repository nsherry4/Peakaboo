package peakaboo.controller.mapper.mapdisplay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;
import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.Range;
import scitypes.Spectrum;

/**
 * Represents a selection of points, which may not be contiguous 
 * @author NAS
 *
 */

public class PointsSelection extends EventfulType<String>{

	private List<Integer> indexes = new ArrayList<>();
	private MappingController map;
	
	public PointsSelection(MappingController map) {
		this.map = map;
	}
	
	public boolean hasSelection() {
		return indexes.size() > 0;
	}
	
	public void clearSelection() {
		setPoints(new ArrayList<>());
	}
	
	public List<Integer> getPoints() {
		return indexes;
	}

	public void setPoints(List<Integer> indexes) {
		this.indexes = indexes;
		updateListeners(UpdateType.POINT_SELECTION.toString());
	}

	public void makeSelection(Coord<Integer> clickedAt, boolean contiguous) {
		
		Spectrum data = map.getDisplay().getCompositeMapData();
		int w = map.settings.getDataWidth();
		int h = map.settings.getDataHeight();
		GridPerspective<Float> grid = new GridPerspective<Float>(w, h, null);
		float value = grid.get(data, clickedAt.x, clickedAt.y);
		
		List<Integer> points = new ArrayList<>();
		Set<Integer> pointSet = new HashSet<>();
		
		if (! contiguous) {
			//All points, even those not touching
			for (int y : new Range(0, h-1)) {
				for (int x : new Range(0, w-1)) {
					float other = grid.get(data, x, y);
					if (value >= other * 0.8f && value <= other * 1.2f) {
						points.add(grid.getIndexFromXY(x, y));
					}
				}
			}
		} else {
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
					if (value >= other * 0.8f && value <= other * 1.2f) {
						points.add(neighbour);
						pointSet.add(neighbour);
					}
				}

				cursor++;
			}
			
			
		}
		
				
		setPoints(points);
		
	}
	
}
