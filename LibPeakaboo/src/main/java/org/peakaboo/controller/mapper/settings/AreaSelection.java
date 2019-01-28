package org.peakaboo.controller.mapper.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;

import cyclops.Coord;
import cyclops.GridPerspective;
import cyclops.Range;
import eventful.EventfulType;

/**
 * Represents a box-style selection over an area
 * @author NAS
 *
 */
public class AreaSelection extends EventfulType<String> {

	private Coord<Integer> start, end;
	private boolean hasSelection = false;
	
	private MappingController map;
	
	public AreaSelection(MappingController map) {
		this.map = map;
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
			if (dragStart.x >= map.getSettings().getView().getUserDataWidth()) dragStart.x = map.getSettings().getView().getUserDataWidth()-1;
			if (dragStart.y >= map.getSettings().getView().getUserDataHeight()) dragStart.y = map.getSettings().getView().getUserDataHeight()-1;
		}
		
		this.start = dragStart;
		
		updateListeners(UpdateType.AREA_SELECTION.toString());
		
		map.addListener(type -> {
			if (UpdateType.DATA_SIZE.toString().equals(type)) {
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
			if (dragEnd.x >= map.getSettings().getView().getUserDataWidth()) dragEnd.x = map.getSettings().getView().getUserDataWidth()-1;
			if (dragEnd.y >= map.getSettings().getView().getUserDataHeight()) dragEnd.y = map.getSettings().getView().getUserDataHeight()-1;
		}
		
		this.end = dragEnd;
		
		updateListeners(UpdateType.AREA_SELECTION.toString());
	}

	
	/**
	 * generate a list of indexes in the map which are selected
	 */
	public List<Integer> getPoints() {
		trimSelectionToBounds();
		List<Integer> indexes = new ArrayList<>();
		
		if (getStart() == null || getEnd() == null) {
			return Collections.emptyList();
		}
		
		final int xstart = getStart().x;
		final int ystart = getStart().y;
		
		final int xend = getEnd().x;
		final int yend = getEnd().y;

		final GridPerspective<Float> grid = new GridPerspective<Float>(
				map.getSettings().getView().getUserDataWidth(), 
				map.getSettings().getView().getUserDataHeight(), 
				0f);
		
		for (int x : new Range(xstart, xend)) {
			for (int y : new Range(ystart, yend)){
				indexes.add( grid.getIndexFromXY(x, y) );
			}
		}
		
		return indexes;
	}
	
	
	public boolean hasSelection()
	{
		// TODO: this check can be moved further down the line later. There's no reason
		// why we can't make any selections just because that selection is not
		// replottable
		return hasSelection && map.getFiltering().isReplottable();
			
	}


	

	public void setHasBoundingRegion(boolean hasBoundingRegion)
	{
		this.hasSelection = hasBoundingRegion;
		updateListeners(UpdateType.AREA_SELECTION.toString());
	}



	public void clearSelection() {
		setHasBoundingRegion(false);
	}
	
	
	public void trimSelectionToBounds() {
				
		int x = map.getSettings().getView().viewDimensions.x;
		int y = map.getSettings().getView().viewDimensions.y;
		
		if (start != null) {
			if (start.x >= x) start.x = x-1;
			if (start.y >= y) start.y = y-1;
		}
		if (end != null) {
			if (end.x >= x) end.x = x-1;
			if (end.y >= y) end.y = y-1;
		}
		
	}
}
