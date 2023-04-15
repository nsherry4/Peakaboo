package org.peakaboo.framework.stratus.api.hookins;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class WindowDragger extends MouseAdapter {

	private Point initialClick, initialPos;
	private Supplier<Window> windowSupplier;
	private boolean draggable = true;
	
	/**
	 * Automatic constructor which will add all mouse event hooks
	 */
	public WindowDragger(Component c) {
		this(() -> SwingUtilities.getWindowAncestor(c));
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}
	
	/**
	 * Manual constructor with no auto-wiring
	 * @param windowSupplier provides the window to drag
	 */
	public WindowDragger(Supplier<Window> windowSupplier) {
		this.windowSupplier = windowSupplier;
	}
	
	public void mousePressed(MouseEvent e) {
		initialClick = e.getLocationOnScreen();
		Window window = windowSupplier.get();
		initialPos = window.getLocation();
	}
	
	public void mouseDragged(MouseEvent e) {
		Window window = windowSupplier.get();
		
		//Only drag on left mouse button
		if(!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		
		//only drag if we're mouse-down on a draggable component
		if (initialClick == null || !draggable) {
			return;
		}
		
		//dont' move parent window if it's maximized
		if (window instanceof JFrame parent) {
			if ((parent.getExtendedState() & JFrame.MAXIMIZED_HORIZ) == JFrame.MAXIMIZED_HORIZ ||
				(parent.getExtendedState() & JFrame.MAXIMIZED_VERT) == JFrame.MAXIMIZED_VERT) {
				return;
			}
		}

		//Calculate new x,y for window
		Point currentClick = e.getLocationOnScreen();
		int dx = currentClick.x - initialClick.x;
		int dy = currentClick.y - initialClick.y;
		int x = initialPos.x + dx;
		int y = initialPos.y + dy;
		window.setLocation(x, y);
		
	}
	
	public void mouseReleased(MouseEvent e) {
		initialClick = null;
		initialPos = null;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	
	
	
}