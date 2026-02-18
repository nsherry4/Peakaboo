package org.peakaboo.ui.swing.mapping.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.ui.swing.mapping.MapCanvas;

public class MapSelectionListener implements MouseMotionListener, MouseListener, MouseWheelListener {

	private MappingController controller;
	private MapCanvas canvas;
	
	private boolean dragging = false;
	
	public MapSelectionListener(MapCanvas canvas, MappingController controller) {
		this.controller = controller;
		this.canvas = canvas;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// Handle right-click for polygon cancellation
		if (SwingUtilities.isRightMouseButton(e)) {
			if (controller.getSelection().getSelectionType() == org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType.POLYGON) {
				controller.getSelection().cancelInProgressSelection();
			}
			return;
		}

		//left button makes selections, and everything else does nothing
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		
		//there are some maps we can't make a selection on
		if (!  ((controller.getFitting().getActiveMode().isComparable()) && controller.getFiltering().isReplottable())) {
			return;
		}
		
		
		// Ignore double-clicks: no selection mode uses them any more
		if (e.getClickCount() != 1) {
			return;
		}
		Coord<Integer> clickedAt = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
		controller.getSelection().selectPoint(clickedAt, true, e.isControlDown());


	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
			// Skip drag for polygon mode (uses clicks)
			if (controller.getSelection().getSelectionType() == org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType.POLYGON) {
				return;
			}
			dragging = true;
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().startDragSelection(point, e.isControlDown());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && dragging) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().releaseDragSelection(point);
			dragging = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//NOOP
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//NOOP
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Skip for polygon mode
		if (controller.getSelection().getSelectionType() == org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType.POLYGON) {
			return;
		}
		if (SwingUtilities.isLeftMouseButton(e) && dragging) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().addDragSelection(point);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Update preview line for polygon mode
		if (controller.getSelection().getSelectionType() == org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType.POLYGON) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().addDragSelection(point);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent wheel) {
		int moves = wheel.getWheelRotation();
		double factor = Math.pow(1.1f, Math.abs(moves));
		
		float zoom = controller.getSettings().getZoom();
		if (moves < 0) {
			// Scrolled Up
			zoom *= factor;
		} else {
			// Scrolled Down
			zoom /= factor;
		}
		
		// Get mouse position for zoom center
		Coord<Integer> zoomCenter = new Coord<>(wheel.getX(), wheel.getY());
		
		controller.getSettings().setZoom(zoom);
		
		// Update canvas size with the zoom center
		canvas.updateCanvasSize(zoomCenter);
		
	}
	
}
