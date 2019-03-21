package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.eventful.EventfulType;

public class MapSelectionController extends EventfulType<String>  {

	private MappingController mappingController;
	private AreaSelection areaSelection;
	private PointsSelection pointsSelection;
	
	public MapSelectionController(MappingController mappingController) {
		this.mappingController = mappingController;
		
		//create selection models and pass their events along
		areaSelection = new AreaSelection(mappingController);
		areaSelection.addListener(this::updateListeners);
		
		pointsSelection = new PointsSelection(mappingController);
		pointsSelection.addListener(this::updateListeners);
		
	}
	
	public  boolean hasSelection() {
		return areaSelection.hasSelection() || pointsSelection.hasSelection();
	}

	public List<Integer> getPoints() {
		if (areaSelection.hasSelection()) {
			return areaSelection.getPoints();
		}
		if (pointsSelection.hasSelection()) {
			return pointsSelection.getPoints();
		}
		return new ArrayList<>();
	}
	
	public float getNeighbourThreshold() {
		return pointsSelection.getThreshold();
	}
	
	public void setNeighbourThreshold(float threshold) {
		pointsSelection.setThreshold(threshold);
	}
	
	public int getNeighbourPadding() {
		return pointsSelection.getPadding();
	}
	
	public void setNeighbourPadding(int padding) {
		pointsSelection.setPadding(padding);
	}
	
	public void makeNeighbourSelection(Coord<Integer> clickedAt, boolean contiguous, boolean modify) {
		areaSelection.clearSelection();
		pointsSelection.makeSelection(clickedAt, contiguous, modify);
	}
	
	public void makeRectSelectionStart(Coord<Integer> dragStart) {
		pointsSelection.clearSelection();
		areaSelection.setStart(dragStart);
		areaSelection.setEnd(null);
		areaSelection.setHasBoundingRegion(false);
	}
	
	public void makeRectSelectionEnd(Coord<Integer> dragEnd) {
		pointsSelection.clearSelection();
		areaSelection.setEnd(dragEnd);
		areaSelection.setHasBoundingRegion(true);
	}
	
	public SubsetDataSource getSubsetDataSource() {
		SubsetDataSource sds;
		if (areaSelection.hasSelection()) {
			sds = mappingController.getDataSourceForSubset(areaSelection.getStart(), areaSelection.getEnd());
		} else {
			sds = mappingController.getDataSourceForSubset(pointsSelection.getPoints());
		}
		return sds;
	}
	
	
}
