package org.peakaboo.controller.mapper.selection;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.IntPair;

import it.unimi.dsi.fastutil.ints.IntArrayList;

class ShapeSelection extends AbstractSelection {

	private IntArrayList points = new IntArrayList();
	
	private static final int EMPTY = 0;
	private static final int INSIDE = 1;
	private static final int OUTSIDE = 2;
	
	
	public ShapeSelection(MappingController mappingController) {
		super(mappingController);
	}

	@Override
	public IntArrayList startDragSelection(Coord<Integer> point) {
		points.clear();
		return addDragSelection(point);
	}

	@Override
	public IntArrayList addDragSelection(Coord<Integer> point) {
		point = bounded(point);
		GridPerspective<Float> grid = grid();
		int index = grid.getIndexFromXY(point.x, point.y);
		
		// if there are already points in this trace, we want to make sure that the
		// points are all contiguous. If a mouse is moving very fast, we might not get
		// all of the points, so we interpolate
		if (!points.isEmpty()) {
			//check if the last point is touching
			int lastIndex = points.getInt(points.size()-1);
			IntPair lastPoint = grid.getXYFromIndex(lastIndex);
			if (Math.abs(lastPoint.first - point.x) > 1 || Math.abs(lastPoint.second - point.y) > 1) {
				interpolate(lastIndex, index, grid);
			}
		}
		
		if (!points.contains(index)) {
			points.add(index);
		}
		return points;
	}

	public IntArrayList releaseDragSelection(Coord<Integer> point) {
		//add the last point
		addDragSelection(point);
		
		//interpolate between the first and last points
		interpolate(points.getInt(0), points.getInt(points.size()-1), grid());
		
		//fill in the traced area now that the user is done making the selection
		fillTrace();
		
		return points;
	}
	
	private void fillTrace() {
		GridPerspective<Float> grid = grid();
		
		//we establish some pixels which are definitely outside the shape and then 'flood-fill' the remainder by adjacency
		Set<Integer> outside = new HashSet<>();
		Set<Integer> inside = new HashSet<>();
		inside.addAll(points);

		//we start by adding all edge pixels which are not included in the trace
		//X - top & bottom
		for (int x = 0; x < grid.width; x++) {
			int index = grid.getIndexFromXY(x, 0);
			if (!inside.contains(index)) {
				outside.add(index);
			}
			index = grid.getIndexFromXY(x, grid.height-1);
			if (!inside.contains(index)) {
				outside.add(index);
			}
		}
		//Y - left and right
		for (int y = 0; y < grid.height; y++) {
			int index = grid.getIndexFromXY(0, y);
			if (!inside.contains(index)) {
				outside.add(index);
			}
			index = grid.getIndexFromXY(grid.width-1, y);
			if (!inside.contains(index)) {
				outside.add(index);
			}
		}

		//if there *are* no outside edge points, then the entire area is selected
		if (outside.isEmpty()) {
			points.clear();
			for (int i = 0; i < grid.width*grid.height; i++) {
				points.add(i);
			}
			return;
		}

		//if there *are* outside edge points, we flood fill in the rest of the outside
		//create an array representing the selection state of each pixel
		// * 0 = empty
		// * 1 = inside
		// * 2 = outside
		IntArrayList values = new IntArrayList();
		for (int i = 0; i < grid.width*grid.height; i++) {
			values.add(EMPTY);
		}
		//set all inside points
		for (int i : points) {
			values.set(i, INSIDE); 
		}
		//set all outside points
		for (int i : outside) {
			values.set(i, OUTSIDE);
		}
		
		//then floodfill the values matrix from each original outside point
		Deque<Integer> stack = new ArrayDeque<>();
		stack.addAll(outside);
		floodFill(values, stack, grid);
		
		//read the value matrix and use it to build a new points list
		points.clear();
		for (int i = 0; i < grid.width*grid.height; i++) {
			if (values.getInt(i) != OUTSIDE) {
				points.add(i);
			}
		}
	}
	
	private void floodFill(IntArrayList values, Deque<Integer> stack, GridPerspective<Float> grid) {	
		
		/*
		 * We track all pixels we've visited to or are going to visit. This ensures we
		 * only visit each pixel once and provides a fast 'contains' check so that we
		 * don't put the same pixel in the stack more than once.
		 */
		Set<Integer> visiting = new HashSet<>(stack);
		
		while (!stack.isEmpty()) {
			int index = stack.pop();
			values.set(index, OUTSIDE);

			//recurse

			int[] neighbours = new int[] { 
					grid.north(index), 
					grid.south(index), 
					grid.east(index), 
					grid.west(index) 
				};
			
			for (int i : neighbours) {
				if (i >= 0 && values.getInt(i) == EMPTY && !visiting.contains(i)) { 
					stack.add(i);
					visiting.add(i);
				}
			}
						
		}
		
	
	}
	
	
	//Given two trace points, interpolate any missing line points between them
	private void interpolate(int i1, int i2, GridPerspective<Float> grid) {
		IntPair p1 = grid.getXYFromIndex(i1);
		IntPair p2 = grid.getXYFromIndex(i2);
				
		int x1 = p1.first;
		int y1 = p1.second;
		int x2 = p2.first;
		int y2 = p2.second;

		float distanceX = x2 - x1;
		float distanceY = y2 - y1;
		
		
		
		/*
		 * Calculate step count required to not miss any pixels
		 */
		
		//absolute value of how much we should advance along the line to get all the pixels
		float advance;
		if (distanceX == 0 || distanceY == 0) {
			advance = 0.5f;
		} else {
			advance = Math.abs(distanceY / distanceX);
			if (advance > 1f) { advance = 1f/advance; }
			advance /= 2f;
		}
		//how long the line between the two points is: a^2 = b^2 + c^2
		float distance = (float) Math.sqrt(distanceY*distanceY + distanceX*distanceX);
		//how many advance steps will we need to cover the distance
		float steps = distance / advance;		
		
		//the amount to advance x and y by each step
		float advanceX = distanceX / steps;
		float advanceY = distanceY / steps;
		
		//advance along the line and add all the pixels we encounter
		float x = x1;
		float y = y1;
		int ix;
		int iy;
		int index;
		for (float pos = 0f; pos < distance; pos += advance) {
			
			ix = Math.round(x);
			iy = Math.round(y);
			index = grid.getIndexFromXY(ix, iy);
			if (!points.contains(index)) {
				points.add(index);
			}
			
			x += advanceX;
			y += advanceY;
		}
		
		
	}
	
	private GridPerspective<Float> grid() {
		Coord<Integer> mapSize = mapSize();
		return new GridPerspective<>(mapSize.x, mapSize.y, 0f);
	}
	
	private Coord<Integer> bounded(Coord<Integer> point) {
		Coord<Integer> trimmed = new Coord<>(point);
		
		Coord<Integer> size = mapSize();
		int width = size.x;
		int height = size.y;

		if (point.x >= width) trimmed.x = width-1;
		if (point.y >= height) trimmed.y = height-1;
		if (point.x < 0) trimmed.x = 0;
		if (point.y < 0) trimmed.y = 0;

		return trimmed;		
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.empty();
	}


	@Override
	public IntArrayList selectPoint(Coord<Integer> clickedAt, boolean singleSelect) {
		points.clear();
		return points;
	}


}
