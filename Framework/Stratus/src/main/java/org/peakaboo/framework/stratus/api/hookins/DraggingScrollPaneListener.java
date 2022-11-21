package org.peakaboo.framework.stratus.api.hookins;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class DraggingScrollPaneListener implements MouseMotionListener, MouseListener {

	public enum Buttons {
		LEFT,
		RIGHT,
		MIDDLE
	}
	
	private Point			p0;
	private boolean			dragging;

	private JViewport		viewPort;
	private Point			scrollPosition;
	private JComponent		canvas;

	private List<Buttons>	buttons;
	
	
	Cursor oldCursor;
	
	public boolean dragX = true, dragY = true;

	public DraggingScrollPaneListener(JViewport viewport, JComponent canvas, Buttons... buttons) {
		this(viewport, canvas, Arrays.asList(buttons));
	}
	
	/**
	 * All you need to do is call this constructor.
	 * @param viewport the viewport the component is in
	 * @param canvas the component to drag-scroll
	 * @param buttons The mouse buttons to respond to
	 */
	public DraggingScrollPaneListener(JViewport viewport, JComponent canvas, List<Buttons> buttons) {
		
		this.buttons = buttons;

		viewPort = viewport;
		scrollPosition = viewPort.getViewPosition();
		this.canvas = canvas;
		
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		
	}
	
	private Point getPoint(MouseEvent e) {
		Point p = e.getPoint();
		if (dragX) p.x += canvas.getLocationOnScreen().x;
		if (dragY) p.y += canvas.getLocationOnScreen().y;

		return p;
	}


	private void update(MouseEvent e) {

		if (p0 == null) return;
		
		Point p1 = getPoint(e);
		int dx = p1.x - p0.x;
		int dy = p1.y - p0.y;
		
		p0 = getPoint(e);
		
		
		
		if (dragX) {
			scrollPosition.x -= dx;
			//can't scroll to a position less than 0
			if (scrollPosition.x < 0) {
				scrollPosition.x = 0;
				
			//if the control isn't as large as the viewport, don't allow scrolling
			} else if (canvas.getWidth() <= viewPort.getWidth()) {
				scrollPosition.x = 0;
				
			//else if the scroll position is beyond the end of the control, cap it
			} else if (scrollPosition.x > canvas.getWidth() - viewPort.getWidth()) {
				scrollPosition.x = canvas.getWidth() - viewPort.getWidth();
			}
		}
		
		
		
		if (dragY) {
			scrollPosition.y -= dy;
			//can't scroll to a position less than 0
			if (scrollPosition.y < 0) {
				scrollPosition.y = 0;
				
			//if the control isn't as large as the viewport, don't allow scrolling
			} else if (canvas.getHeight() <= viewPort.getHeight()) {
				scrollPosition.y = 0;
				
			//if the scroll position is beyond the end of the control, cap it
			}else if (scrollPosition.y > canvas.getHeight() - viewPort.getHeight()) {
				scrollPosition.y = canvas.getHeight() - viewPort.getHeight();
			}
		}


		viewPort.setViewPosition(scrollPosition);

	}


	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			update(e);
		}
	}


	public void mouseMoved(MouseEvent e) {
		if (dragging) {
			update(e);
		}
	}


	public void mouseClicked(MouseEvent e){}


	public void mouseEntered(MouseEvent e){}


	public void mouseExited(MouseEvent e){}


	public void mousePressed(MouseEvent e) {
		if (!buttonsMatch(e)) return;
		if (dragging) return;
		
		p0 = getPoint(e);
		scrollPosition = viewPort.getViewPosition();
		dragging = true;
		oldCursor = canvas.getCursor();
		canvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}


	public void mouseReleased(MouseEvent e) {
		
		if (!buttonsMatch(e)) return;
		
		update(e);
		dragging = false;
		p0 = null;
		canvas.setCursor(oldCursor);
	}

	private boolean buttonsMatch(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && buttons.contains(Buttons.LEFT)) return true;
		if (SwingUtilities.isMiddleMouseButton(e) && buttons.contains(Buttons.MIDDLE)) return true;
		if (SwingUtilities.isRightMouseButton(e) && buttons.contains(Buttons.RIGHT)) return true;
		return false;
	}
	
}
