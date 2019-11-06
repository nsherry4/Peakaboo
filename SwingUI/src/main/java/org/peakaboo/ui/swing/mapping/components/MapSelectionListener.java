package org.peakaboo.ui.swing.mapping.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.ui.swing.mapping.MapCanvas;

public class MapSelectionListener implements MouseMotionListener, MouseListener {

	private MappingController controller;
	private MapCanvas canvas;
	
	public MapSelectionListener(MapCanvas canvas, MappingController controller) {
		this.controller = controller;
		this.canvas = canvas;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		//left button makes selections, and everything else does nothing
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		
		//there are some maps we can't make a selection on
		MapModes displayMode = controller.getFitting().getMapDisplayMode();
		if (!  ((displayMode == MapModes.COMPOSITE || displayMode == MapModes.RATIO) && controller.getFiltering().isReplottable())) {
			return;
		}
		
		
		Coord<Integer> clickedAt = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
		if (e.isControlDown()) {
			if (e.getClickCount() == 1) {
				controller.getSelection().selectPoint(clickedAt, true, true);
			} else if (e.getClickCount() == 2) {
				//Double clicks only get run after a single click gets run. If CTRL is down, that means we need to
				//undo the action caused by the previous (improper) single-click, so we re-run the contiguous
				//selection modification to perform the reverse modification.
				controller.getSelection().selectPoint(clickedAt, true, true);
				controller.getSelection().selectPoint(clickedAt, false, true);
			}
		} else {
			controller.getSelection().selectPoint(clickedAt, e.getClickCount() == 1, false);
		}


	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && !e.isControlDown()) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().startDragSelection(point);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().releaseDragSelection(point);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			Coord<Integer> point = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
			controller.getSelection().addDragSelection(point);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
