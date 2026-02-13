package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.GridPerspective;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Polygon selection using click-based vertex placement.
 * Users click to add vertices, polygon auto-closes when clicking near the first vertex.
 * Uses flood-fill algorithm from AbstractShapeSelection to determine interior.
 */
class PolygonSelection extends AbstractShapeSelection {

	private List<Coord<Integer>> vertices = new ArrayList<>();
	private Coord<Integer> previewPoint = null;
	private boolean closed = false;

	private static final int PROXIMITY_THRESHOLD = 10; // pixels

	public PolygonSelection(MappingController mappingController) {
		super(mappingController);
	}

	@Override
	public IntArrayList selectPoint(Coord<Integer> clickedAt, boolean singleSelect) {
		// If already closed, start a new polygon
		if (closed) {
			reset();
		}

		clickedAt = bounded(clickedAt);

		// Check if we should close the polygon (near first vertex, >= 3 vertices)
		if (vertices.size() >= 3 && isNear(clickedAt, vertices.get(0))) {
			closePolygon();
			return points;
		}

		// Add vertex
		vertices.add(clickedAt);

		// CLAUDETODO We generally want to see all lines drawn so far. We may
		// want to rework the interpolation function to make this easier
		// especially if we want to cache the interpolation between existing vertices

		// Interpolate edge from previous vertex to new vertex
		if (vertices.size() > 1) {
			GridPerspective<Float> grid = grid();
			Coord<Integer> prev = vertices.get(vertices.size() - 2);
			int idx1 = grid.getIndexFromXY(prev.x, prev.y);
			int idx2 = grid.getIndexFromXY(clickedAt.x, clickedAt.y);
			interpolate(idx1, idx2, grid);
		} else {
			// First vertex - just add it as a point
			GridPerspective<Float> grid = grid();
			int index = grid.getIndexFromXY(clickedAt.x, clickedAt.y);
			if (!points.contains(index)) {
				points.add(index);
			}
		}

		return getPreviewPoints();
	}

	@Override
	public IntArrayList addDragSelection(Coord<Integer> point) {
		// Update preview point for mouse movement (shows preview line to cursor)
		previewPoint = bounded(point);
		return getPreviewPoints();
	}

	@Override
	public IntArrayList startDragSelection(Coord<Integer> point) {
		// No-op for polygon mode (uses clicks, not drags)
		return points;
	}

	@Override
	public IntArrayList releaseDragSelection(Coord<Integer> point) {
		// No-op for polygon mode (polygons close on proximity, not drag release)
		return points;
	}

	@Override
	public void reset() {
		super.reset(); // Clear points from AbstractShapeSelection
		vertices.clear();
		previewPoint = null;
		closed = false;
	}

	@Override
	public Optional<Group> getParameters() {
		return Optional.empty();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Close the polygon by interpolating between last and first vertex,
	 * then flood-filling the interior.
	 */
	private void closePolygon() {
		if (vertices.size() < 3) {
			return; // Can't close with < 3 vertices
		}

		GridPerspective<Float> grid = grid();

		// Interpolate closing edge from last vertex back to first
		Coord<Integer> first = vertices.get(0);
		Coord<Integer> last = vertices.get(vertices.size() - 1);
		int idx1 = grid.getIndexFromXY(last.x, last.y);
		int idx2 = grid.getIndexFromXY(first.x, first.y);
		interpolate(idx1, idx2, grid);

		// Fill the interior using shared flood-fill algorithm
		fillTrace();

		closed = true;
		previewPoint = null;
	}

	/**
	 * Build preview including edges and optional preview line to cursor.
	 */
	private IntArrayList getPreviewPoints() {
		if (closed || vertices.isEmpty()) {
			return points;
		}

		// Return edges plus preview line from last vertex to current mouse position
		IntArrayList preview = new IntArrayList(points);

		if (previewPoint != null && vertices.size() > 0) {
			// Add preview line from last vertex to mouse cursor
			GridPerspective<Float> grid = grid();
			Coord<Integer> last = vertices.get(vertices.size() - 1);
			int idx1 = grid.getIndexFromXY(last.x, last.y);
			int idx2 = grid.getIndexFromXY(previewPoint.x, previewPoint.y);

			// Temporarily interpolate preview line (don't modify points field)
			IntArrayList previewLine = new IntArrayList();
			// Save current points state
			IntArrayList savedPoints = points;
			points = previewLine;
			interpolate(idx1, idx2, grid);
			// Restore points and add preview to result
			points = savedPoints;
			preview.addAll(previewLine);
		}

		return preview;
	}

	/**
	 * Check if two points are within proximity threshold (for auto-close detection).
	 */
	private boolean isNear(Coord<Integer> p1, Coord<Integer> p2) {
		double distance = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
		return distance < PROXIMITY_THRESHOLD;
	}

}
