package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.eventful.EventfulType;

public class MapSelectionController extends EventfulType<MapUpdateType> {

	private MappingController map;
	
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
	private List<Integer> newSelection = new ArrayList<>();
	private boolean modify = false;
	private Coord<Integer> dragFocalPoint;
	
	public MapSelectionController(MappingController mappingController) {
		this.map = mappingController;
		
		//create selection models and pass their events along
		areaSelection = new DragSelection(mappingController);
		similarSelection = new SimilarSelection(mappingController);
		shapeSelection = new ShapeSelection(mappingController);
		
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



	public boolean hasSelection() {
		return currentSelection.size() > 0 || newSelection.size() > 0;
	}

	public List<Integer> getPoints() {
		//return currentSelection;
		return trimSelectionToBounds(mergeSelections(dragFocalPoint, modify));
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
		return hasSelection() && map.getFitting().getActiveMode().isReplottable() && map.rawDataController.isReplottable();
	}

	public void clearSelection() {
		newSelection.clear();
		currentSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	

	public void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify) {
		if (!isSelectable()) {
			return;
		}
		newSelection = getSelection().selectPoint(clickedAt, singleSelect);
		currentSelection = mergeSelections(clickedAt, modify);
		newSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	
	
	public void startDragSelection(Coord<Integer> point, boolean modify) {
		if (!isSelectable()) {
			return;
		}
		if (!modify) clearSelection();
		newSelection = getSelection().startDragSelection(point);
		this.modify = modify;
		this.dragFocalPoint = point;
		updateListeners(MapUpdateType.SELECTION);
	}
	
	public void addDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			return;
		}
		
		newSelection = getSelection().addDragSelection(point);
		updateListeners(MapUpdateType.SELECTION);
	}
	
	public void releaseDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			return;
		}
		
		newSelection = getSelection().releaseDragSelection(point);
		currentSelection = mergeSelections(dragFocalPoint, modify);
		newSelection.clear();
		this.modify = false;
		updateListeners(MapUpdateType.SELECTION);
	}
	
	private List<Integer> mergeSelections(Coord<Integer> point, boolean modify) {
		List<Integer> merged = new ArrayList<>();
		if (!modify) {
			if (newSelection.size() > 0) {
				merged.addAll(newSelection);
			} else {
				merged.addAll(currentSelection);
			}
		} else {
			Set<Integer> set = new HashSet<>();
			GridPerspective<Float> grid = new GridPerspective<Float>(
					map.getUserDimensions().getUserDataWidth(), 
					map.getUserDimensions().getUserDataHeight(), 
					0f);
			
			set.addAll(currentSelection);
			if (set.contains(grid.getIndexFromXY(point)))	{
				//if it already contains the focalpoint, do subtraction
				set.removeAll(newSelection);
			} else {
				//it doesn't already contain the focal point, so add
				set.addAll(newSelection);
			}
			merged.addAll(set);

		}
		return merged;

	}
	
	//TODO:
	public SubsetDataSource getSubsetDataSource() {
		if (isRectangular()) {
			return map.getDataSourceForSubset(getRectangleStart(), getRectangleEnd());
		} else {
			return map.getDataSourceForSubset(getPoints());
		}
	}
	
	
	private Coord<Integer> getRectangleEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	private Coord<Integer> getRectangleStart() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isRectangular() {
		// TODO Auto-generated method stub
		return false;
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

	public Optional<Group> getParameters() {
		return getSelection().getParameters();
	}

	
	private boolean isSelectable() {
		return map.getFitting().getActiveMode().isSelectable();
	}
	
	public List<Integer> trimSelectionToBounds(List<Integer> points) {
		
		Coord<Integer> dimensions = map.getUserDimensions().getDimensions();
		int x = dimensions.x;
		int y = dimensions.y;
		int size = x*y;
		
		List<Integer> trimmed = new ArrayList<>();
		for (int i : points) {
			if (i >= 0 && i < size) {
				trimmed.add(i);
			}
		}
		return trimmed;
	}
	
}
