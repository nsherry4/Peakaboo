package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Range;

/**
 * Represents a box-style selection over an area
 * @author NAS
 *
 */
class DragSelection extends AbstractSelection {

	private Coord<Integer> start, end;
		
	public DragSelection(MappingController map) {
		super(map);
	}
		
	public Coord<Integer> getStart()
	{
		return start;
	}
	
	public void setStart(Coord<Integer> dragStart)
	{
		if (dragStart != null) 
		{
			if (dragStart.x < 0) dragStart.x = 0;
			if (dragStart.y < 0) dragStart.y = 0;
			if (dragStart.x >= size().x) dragStart.x = size().x-1;
			if (dragStart.y >= size().y) dragStart.y = size().y-1;
		}
		
		this.start = dragStart;
		
		//TODO: move up
		map.addListener(type -> {
			if (type == MapUpdateType.DATA_SIZE) {
				trimSelectionToBounds();
			}
		});
	}

	
	
	public Coord<Integer> getEnd()
	{
		return end;
	}

	public void setEnd(Coord<Integer> dragEnd)
	{
		if (dragEnd != null)
		{
			if (dragEnd.x < 0) dragEnd.x = 0;
			if (dragEnd.y < 0) dragEnd.y = 0;
			if (dragEnd.x >= size().x) dragEnd.x = size().x-1;
			if (dragEnd.y >= size().y) dragEnd.y = size().y-1;
		}
		
		this.end = dragEnd;
	}

	
	/**
	 * generate a list of indexes in the map which are selected
	 */
	private List<Integer> getPoints() {
		trimSelectionToBounds();
		List<Integer> indexes = new ArrayList<>();
		
		if (getStart() == null || getEnd() == null || getStart().equals(getEnd())) {
			return indexes;
		}
		
		final GridPerspective<Float> grid = new GridPerspective<>(size().x, size().y, 0f);
		
		switch (map.getSelection().getSelectionType()) {
		case ELLIPSE:
			return getPointsEllipse(grid);
		case RECTANGLE:
			return getPointsRectangle(grid);
		case SIMILAR:
		case SHAPE:
		default:
			throw new IllegalArgumentException("Not implemented"); //not implemented here, see the other Selection impls
		
		}

	}
	
	private List<Integer> getPointsRectangle(GridPerspective<Float> grid) {
		List<Integer> indexes = new ArrayList<>();
		
		final int xstart = getStart().x;
		final int ystart = getStart().y;
		
		final int xend = getEnd().x;
		final int yend = getEnd().y;
		
		for (int y : new Range(ystart, yend)) {
			for (int x : new Range(xstart, xend)) {
				indexes.add( grid.getIndexFromXY(x, y) );
			}
		}
		
		return indexes;
		
	}
	
	private List<Integer> getPointsEllipse(GridPerspective<Float> grid) {
		List<Integer> indexes = new ArrayList<>();
		
		final int xstart = Math.min(getStart().x, getEnd().x);
		final int ystart = Math.min(getStart().y, getEnd().y);
		final int xend = Math.max(getStart().x, getEnd().x);
		final int yend = Math.max(getStart().y, getEnd().y);

		final int width = (xend - xstart) + 1;
		final int height = (yend - ystart) + 1;
		final float a = width/2f;
		final float b = height/2f;

		for (int x : new Range(xstart, xend)) {
			float x0 = (x - xstart) - a + 0.5f; //0.5 for the middle of the pixel
			for (int y : new Range(ystart, yend)){
				float y0 = (y - ystart) - b + 0.5f;	
				float dist = (x0*x0) / (a*a) + (y0*y0) / (b*b);
				if (dist <= 1f) {
					indexes.add( grid.getIndexFromXY(x, y) );
				}
			}
		}
		
		return indexes;
	}
		
	@Override
	public Optional<Group> getParameters() {
		return Optional.empty();
	}
	
	public void trimSelectionToBounds() {
		
		Coord<Integer> size = size();
		int x = size.x;
		int y = size.y;
		
		if (start != null) {
			if (start.x >= x) start.x = x-1;
			if (start.y >= y) start.y = y-1;
		}
		if (end != null) {
			if (end.x >= x) end.x = x-1;
			if (end.y >= y) end.y = y-1;
		}
		
	}

	@Override
	public List<Integer> selectPoint(Coord<Integer> clickedAt, boolean singleSelect) {
		start = null;
		end = null;
		return Collections.emptyList();
	}

	@Override
	public List<Integer> startDragSelection(Coord<Integer> point) {
		setStart(point);
		setEnd(null);
		return getPoints();
	}

	@Override
	public List<Integer> addDragSelection(Coord<Integer> point) {
		setEnd(point);
		return getPoints();
	}

	@Override
	public List<Integer> releaseDragSelection(Coord<Integer> point) {
		return addDragSelection(point);
	}
	
	
}
