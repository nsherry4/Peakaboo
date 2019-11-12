package org.peakaboo.controller.mapper.selection;

import java.nio.channels.FileChannel.MapMode;
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
import org.peakaboo.framework.cyclops.IntPair;
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
		
		//TODO: This should be better at preserving selections
		map.addListener(m -> {
			if (currentMode == null) {
				currentMode = map.getFitting().getActiveMode();
			}
			if (map.getFitting().getActiveMode() != currentMode) {
				currentMode = map.getFitting().getActiveMode();
				clearSelection();
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
		return currentSelection.size() > 0 || newSelection.size() > 0;
	}

	public List<Integer> getPoints(boolean translated) {
		//return currentSelection;
		List<Integer> points = mergeSelections(dragFocalPoint, modify);
		if (translated) {
			return trimSelectionToBounds(translate(points), true);
		} else {
			return trimSelectionToBounds(points, false);
		}
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
		return hasSelection() && map.getFitting().getActiveMode().isTranslatable() && map.rawDataController.isReplottable();
	}

	public void clearSelection() {
		newSelection.clear();
		currentSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	

	public void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify) {
		if (!isTranslatable()) {
			return;
		}
		newSelection = getSelection().selectPoint(clickedAt, singleSelect);
		currentSelection = mergeSelections(clickedAt, modify);
		newSelection.clear();
		updateListeners(MapUpdateType.SELECTION);
	}
	
	
	public void startDragSelection(Coord<Integer> point, boolean modify) {
		if (!isTranslatable()) {
			return;
		}
		if (!modify) clearSelection();
		newSelection = getSelection().startDragSelection(point);
		this.modify = modify;
		this.dragFocalPoint = point;
		updateListeners(MapUpdateType.SELECTION);
	}
	
	public void addDragSelection(Coord<Integer> point) {
		if (!isTranslatable()) {
			return;
		}
		
		newSelection = getSelection().addDragSelection(point);
		updateListeners(MapUpdateType.SELECTION);
	}
	
	public void releaseDragSelection(Coord<Integer> point) {
		if (!isTranslatable()) {
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
			GridPerspective<Float> grid = new GridPerspective<Float>(size().x, size().y, 0f);
			
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
		if (isRectangular()) {
			return map.getDataSourceForSubset(getRectangleStart(), getRectangleEnd());
		} else {
			return map.getDataSourceForSubset(getPoints(true));
		}
	}
	
	
	private Coord<Integer> getRectangleEnd() {
		//we need to use the real dimensions here because non-spacial 
		//map modes will have to translate back to actual map points
		//before we can deal with it as a rectangular area of spectra
		GridPerspective<Float> grid = new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(), 
				map.getUserDimensions().getUserDataHeight(), 
				0f);
		List<Integer> points = getPoints(true);
		int maxx = 0;
		int maxy = 0;
		for (int i : points) {
			IntPair coord = grid.getXYFromIndex(i);
			maxx = Math.max(maxx, coord.first);
			maxy = Math.max(maxy, coord.second);
		}
		return new Coord<Integer>(maxx, maxy);
	}

	private Coord<Integer> getRectangleStart() {
		//we need to use the real dimensions here because non-spacial 
		//map modes will have to translate back to actual map points
		//before we can deal with it as a rectangular area of spectra
		GridPerspective<Float> grid = new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(), 
				map.getUserDimensions().getUserDataHeight(), 
				0f);
		List<Integer> points = getPoints(true);
		int minx = grid.width;
		int miny = grid.height;
		for (int i : points) {
			IntPair coord = grid.getXYFromIndex(i);
			minx = Math.min(minx, coord.first);
			miny = Math.min(miny, coord.second);
		}
		return new Coord<Integer>(minx, miny);
		
	}

	private boolean isRectangular() {
		//we need to use the real dimensions here because non-spacial 
		//map modes will have to translate back to actual map points
		//before we can deal with it as a rectangular area of spectra
		GridPerspective<Float> grid = new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(), 
				map.getUserDimensions().getUserDataHeight(), 
				0f);
		List<Integer> points = getPoints(true);
		int minx = grid.width;
		int miny = grid.height;
		int maxx = 0;
		int maxy = 0;
		boolean selected[] = new boolean[grid.size()];
		for (int i : points) {
			selected[i] = true;
			IntPair coord = grid.getXYFromIndex(i);
			minx = Math.min(minx, coord.first);
			miny = Math.min(miny, coord.second);
			maxx = Math.max(maxx, coord.first);
			maxy = Math.max(maxy, coord.second);
		}
		
		for (int x = minx; x <= maxx; x++) {
			for (int y = miny; y <= maxy; y++) {
				int index = grid.getIndexFromXY(x, y);
				if (!selected[index]) {
					return false;
				}
			}
		}

		return true;
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

	
	private boolean isTranslatable() {
		return map.getFitting().getActiveMode().isTranslatable();
	}
	
	public List<Integer> trimSelectionToBounds(List<Integer> points, boolean translated) {
		
		//This is a bit tricky -- the fitting map mode generally comes before filtering
		//since it has to select which transition series get included and how, but it also
		//comes after, in the sense that once filtering is done, the mode determines what 
		//kind of processing is done to turn the maps into displayed data.
		Coord<Integer> dimensions;
		if (translated) {
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
	
	private List<Integer> translate(List<Integer> points) {
		return map.getFitting().getActiveMode().translateSelection(points);
	}
	
}
