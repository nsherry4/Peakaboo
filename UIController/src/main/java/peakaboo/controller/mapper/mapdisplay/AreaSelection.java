package peakaboo.controller.mapper.mapdisplay;

import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;
import scitypes.Coord;

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
			if (dragStart.x >= map.settings.getDataWidth()) dragStart.x = map.settings.getDataWidth()-1;
			if (dragStart.y >= map.settings.getDataHeight()) dragStart.y = map.settings.getDataHeight()-1;
		}
		
		this.start = dragStart;
		
		updateListeners(UpdateType.BOUNDING_REGION.toString());
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
			if (dragEnd.x >= map.settings.getDataWidth()) dragEnd.x = map.settings.getDataWidth()-1;
			if (dragEnd.y >= map.settings.getDataHeight()) dragEnd.y = map.settings.getDataHeight()-1;
		}
		
		this.end = dragEnd;
		
		updateListeners(UpdateType.BOUNDING_REGION.toString());
	}

	
	public boolean hasSelection()
	{
		return hasSelection;
			
	}


	

	public void setHasBoundingRegion(boolean hasBoundingRegion)
	{
		this.hasSelection = hasBoundingRegion;
		updateListeners(UpdateType.BOUNDING_REGION.toString());
	}



	public void clearSelection() {
		setHasBoundingRegion(false);
	}
	
}
