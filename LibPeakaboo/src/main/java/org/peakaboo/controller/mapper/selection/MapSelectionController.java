package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.ModeController;
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
	
	private ModeController currentMode;
	
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
		
		map.addListener(m -> {
			
			if (!isSelectable() && hasSelection()) {
				clearSelection();
			}
			
			if (currentMode == null) {
				currentMode = map.getFitting().getActiveMode();
			}
			ModeController newMode = map.getFitting().getActiveMode();
			ModeController oldMode = currentMode;
			currentMode = newMode;
			
			if (newMode == oldMode) {
				return;
			} else if (!newMode.isSpatial()) {
				//new mode is not spatial, so bringing over selected points makes no sense
				clearSelection();
			} else if (oldMode.isSpatial()) {
				//the old mode and new mode are both spatial, so just keep the points
			} else if (!oldMode.isSpatial()) {
				//the old mode is not spatial, so we have to translate the points back to 
				//spatial points before keeping them
				currentSelection = oldMode.translateSelectionToSpatial(currentSelection);
			}

		});
		
		
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
		return ! (currentSelection.isEmpty() && newSelection.isEmpty());
	}

	public List<Integer> getPoints(boolean translated) {
		List<Integer> points = mergeSelections(dragFocalPoint, modify);
		if (translated) {
			points = trimSelectionToBounds(translateToSpatial(points), true);
		} else {
			points = trimSelectionToBounds(points, false);
		}
		/*
		 * Now we should have points which are spatial (map back to real points in the
		 * underlying data source). We can proceed to filtering out any spatial index
		 * that doesn't have a real data point backing it
		 */
		List<Integer> invalidPoints = map.rawDataController.getInvalidPoints();
		points.removeAll(invalidPoints);
		
		return points;
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
		return hasSelection() && isSelectable();
	}
	
	public boolean isSelectable() {
		return map.getFitting().getActiveMode().isTranslatableToSpatial() //The current mapping mode can map it back to source spectra 
				//&& map.rawDataController.isReplottable() //The original data source supports replotting
				&& map.getFiltering().isReplottable(); //The filters applied don't prohibit replotting
	}

	public void clearSelection() {
		newSelection.clear();
		currentSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	

	public void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify) {
		if (!isSelectable()) {
			clearSelection();
			return;
		}
		newSelection = getSelection().selectPoint(clickedAt, singleSelect);
		currentSelection = mergeSelections(clickedAt, modify);
		newSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	
	
	public void startDragSelection(Coord<Integer> point, boolean modify) {
		if (!isSelectable()) {
			clearSelection();
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
			clearSelection();
			return;
		}
		
		newSelection = getSelection().addDragSelection(point);
		updateListeners(MapUpdateType.SELECTION);
	}
	
	public void releaseDragSelection(Coord<Integer> point) {
		if (!isSelectable()) {
			clearSelection();
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
			if (newSelection.isEmpty()) {
				merged.addAll(currentSelection);
			} else {
				merged.addAll(newSelection);				
			}
		} else {
			Set<Integer> set = new HashSet<>();
			GridPerspective<Float> grid = new GridPerspective<>(size().x, size().y, 0f);
			
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
	
	private Coord<Integer> size() {
		return map.getFitting().getActiveMode().getData().getSize();
	}
	

	public SubsetDataSource getSubsetDataSource() {
		return map.getDataSourceForSubset(getPoints(true));
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
	
	public List<Integer> trimSelectionToBounds(List<Integer> points, boolean spatial) {
		
		//This is a bit tricky -- the fitting map mode generally comes before filtering
		//since it has to select which transition series get included and how, but it also
		//comes after, in the sense that once filtering is done, the mode determines what 
		//kind of processing is done to turn the maps into displayed data.
		Coord<Integer> dimensions;
		if (spatial) {
			dimensions = map.getUserDimensions().getDimensions();
		} else {
			dimensions = map.getFitting().getActiveMode().getData().getSize();
		}
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
	
	/**
	 * Given a list of selected points (for the current map mode), translate the points back
	 * to the spectra that generated those points. If there is no translation, the points will
	 * simply be returned.
	 */
	private List<Integer> translateToSpatial(List<Integer> points) {
		return map.getFitting().getActiveMode().translateSelectionToSpatial(points);
	}
	
}
