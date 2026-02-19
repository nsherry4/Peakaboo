package org.peakaboo.controller.mapper.selection;

import java.util.Optional;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.GridPerspective;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Freehand shape selection using continuous drag to trace edges.
 * Uses flood-fill algorithm from AbstractShapeSelection to determine interior.
 */
class FreehandSelection extends AbstractShapeSelection {

	public FreehandSelection(MappingController mappingController) {
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
			var lastPoint = grid.getXYFromIndex(lastIndex);
			if (Math.abs(lastPoint.first - point.x) > 1 || Math.abs(lastPoint.second - point.y) > 1) {
				points.addAll(interpolate(lastIndex, index, grid));
			}
		}

		if (!points.contains(index)) {
			points.add(index);
		}
		return points;
	}

	@Override
	public IntArrayList releaseDragSelection(Coord<Integer> point) {
		//add the last point
		addDragSelection(point);

		//interpolate between the first and last points
		points.addAll(interpolate(points.getInt(0), points.getInt(points.size()-1), grid()));

		//fill in the traced area now that the user is done making the selection
		fillTrace();

		return points;
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.empty();
	}

	@Override
	public IntArrayList selectPoint(Coord<Integer> clickedAt) {
		points.clear();
		return points;
	}

}
