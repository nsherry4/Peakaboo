package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.eventful.EventfulType;

public class MapSelectionController extends EventfulType<MapUpdateType> implements Selection {

	private MappingController mappingController;
	
	private DragSelection areaSelection;
	private SimilarSelection similarSelection;
	private ShapeSelection shapeSelection;
	
	public enum SelectionType {
		SIMILAR,
		RECTANGLE,
		ELLIPSE,
		SHAPE
	}
	private SelectionType selectionType = SelectionType.RECTANGLE;
	
	private List<Integer> currentSelection = new ArrayList<>();
	
	public MapSelectionController(MappingController mappingController) {
		this.mappingController = mappingController;
		
		//create selection models and pass their events along
		areaSelection = new DragSelection(mappingController);
		areaSelection.addListener(this::onSelectionMessage);
		
		similarSelection = new SimilarSelection(mappingController);
		similarSelection.addListener(this::onSelectionMessage);
		
		shapeSelection = new ShapeSelection(mappingController);
		shapeSelection.addListener(this::onSelectionMessage);
		
	}
	
	private void onSelectionMessage(MapUpdateType update) {
		if (update == MapUpdateType.SELECTION) {
			currentSelection = getSelection().getPoints();
		}
		updateListeners(update);
	}
	
	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
		// don't call onSelectionMessage here because we explicitly don't want to
		// regenerate the points selection
		updateListeners(MapUpdateType.UI_OPTIONS);
		updateListeners(MapUpdateType.SELECTION);
	}



	@Override
	public boolean hasSelection() {
		return getSelection().hasSelection();
	}

	@Override
	public List<Integer> getPoints() {
		return currentSelection;
	}
	
	@Override
	public boolean isReplottable() {
		return hasSelection() && getSelection().isReplottable();
	}

	@Override
	public void clearSelection() {
		areaSelection.clearSelection();
		similarSelection.clearSelection();
		shapeSelection.clearSelection();
	}
	
	public float getNeighbourThreshold() {
		return similarSelection.getThreshold();
	}
	
	public void setNeighbourThreshold(float threshold) {
		similarSelection.setThreshold(threshold);
	}
	
	public int getNeighbourPadding() {
		return similarSelection.getPadding();
	}
	
	public void setNeighbourPadding(int padding) {
		similarSelection.setPadding(padding);
	}
	

	public void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify) {
		if (!mappingController.getFitting().getActiveMode().isSelectable()) {
			return;
		}
		
		switch (selectionType) {
		case ELLIPSE:
		case RECTANGLE:
		case SHAPE:
			break;
		case SIMILAR:
			clearSelection();
			similarSelection.makeSelection(clickedAt, singleSelect, modify);
			break;
		}
		
	}
	
	
	public void startDragSelection(Coord<Integer> point) {
		if (!mappingController.getFitting().getActiveMode().isSelectable()) {
			return;
		}
		
		switch (selectionType) {
		case ELLIPSE:
		case RECTANGLE:
			clearSelection();
			areaSelection.setStart(point);
			areaSelection.setEnd(null);
			areaSelection.setHasBoundingRegion(false);
			break;
		case SHAPE:
			shapeSelection.startTrace(point);
			break;
		case SIMILAR:
			break;
		}
	}
	
	public void addDragSelection(Coord<Integer> point) {
		if (!mappingController.getFitting().getActiveMode().isSelectable()) {
			return;
		}
		
		switch (selectionType) {
		case ELLIPSE:
		case RECTANGLE:
			areaSelection.setEnd(point);
			areaSelection.setHasBoundingRegion(true);
			break;
		case SHAPE:
			shapeSelection.addTrace(point);
			break;
		case SIMILAR:
			break;		
		}

	}
	
	public void releaseDragSelection(Coord<Integer> point) {
		if (!mappingController.getFitting().getActiveMode().isSelectable()) {
			return;
		}
		
		switch (selectionType) {
		case ELLIPSE:
		case RECTANGLE:
			areaSelection.setEnd(point);
			areaSelection.setHasBoundingRegion(true);
			break;
		case SHAPE:
			shapeSelection.endTrace(point);
			break;
		case SIMILAR:
			break;		
		}
	}
	
	
	
	
	public SubsetDataSource getSubsetDataSource() {
		SubsetDataSource sds;
		if (areaSelection.hasSelection()) {
			sds = mappingController.getDataSourceForSubset(areaSelection.getStart(), areaSelection.getEnd());
		} else {
			sds = mappingController.getDataSourceForSubset(similarSelection.getPoints());
		}
		return sds;
	}
	
	
	private Selection getSelection() {
		switch (selectionType) {
		case ELLIPSE:
		case RECTANGLE:
			return areaSelection;
		case SIMILAR:
			return similarSelection;
		case SHAPE:
			return shapeSelection;
		}
		throw new IllegalArgumentException("Unknown selection type");
	}




	
	
}
