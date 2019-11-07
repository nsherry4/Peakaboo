package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.framework.autodialog.model.Group;
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
	
	/**
	 * Indicates if a selection on a map can be reliably translated back to the
	 * original source spectrum. There are a number of reasons this may not be true
	 * including map filters and non-rectangular cropping
	 * 
	 * @return True if the data can be related back to the source spectra, false
	 *         otherwise
	 */
	public boolean isReplottable() {
		return hasSelection() && mappingController.getFitting().getActiveMode().isReplottable() && mappingController.rawDataController.isReplottable();
	}

	@Override
	public void clearSelection() {
		areaSelection.clearSelection();
		similarSelection.clearSelection();
		shapeSelection.clearSelection();
	}
	

	public void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify) {
		if (!isSelectable()) {
			return;
		}
		getSelection().selectPoint(clickedAt, singleSelect, modify);		
	}
	
	
	public void startDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			return;
		}
		clearSelection();
		
		getSelection().startDragSelection(point);
	}
	
	public void addDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			return;
		}
		
		getSelection().addDragSelection(point);
	}
	
	public void releaseDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			return;
		}
		
		getSelection().releaseDragSelection(point);
	}
	
	
	
	@Override
	public SubsetDataSource getSubsetDataSource() {
		return getSelection().getSubsetDataSource();
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

	@Override
	public Optional<Group> getParameters() {
		return getSelection().getParameters();
	}

	
	private boolean isSelectable() {
		return mappingController.getFitting().getActiveMode().isSelectable();
	}
	
}
