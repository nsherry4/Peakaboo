package org.peakaboo.framework.stratus.api.hookins;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
	
	// Momentum-related fields
	private long lastDragTime;
	private double velocityX, velocityY;
	private Timer momentumTimer;
	// Velocity reduction per frame (15% decay)
	private static final double MOMENTUM_DECAY = 0.85;
	// Minimum velocity threshold to stop animation
	private static final double MIN_VELOCITY = 1;
	// Animation frame delay (~40 FPS)
	private static final int MOMENTUM_TIMER_DELAY = 25;
	// Velocity smoothing factor (higher = more smoothing)
	private static final double VELOCITY_SMOOTHING = 0.7;
	// Maximum time gap between samples before resetting velocity (ms)
	private static final long MAX_VELOCITY_TIME_GAP = 100;
	
	
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
		
		// Calculate velocity for momentum with smoothing
		long currentTime = System.currentTimeMillis();
		if (lastDragTime > 0) {
			long deltaTime = currentTime - lastDragTime;
			if (deltaTime > 0 && deltaTime < MAX_VELOCITY_TIME_GAP) {
				double newVelocityX = dx / (double) deltaTime * 1000; // pixels per second
				double newVelocityY = dy / (double) deltaTime * 1000; // pixels per second
				
				// Smooth velocity using weighted average
				velocityX = velocityX * VELOCITY_SMOOTHING + newVelocityX * (1 - VELOCITY_SMOOTHING);
				velocityY = velocityY * VELOCITY_SMOOTHING + newVelocityY * (1 - VELOCITY_SMOOTHING);
			} else if (deltaTime >= MAX_VELOCITY_TIME_GAP) {
				// Reset velocity if too much time has passed
				velocityX = 0;
				velocityY = 0;
			}
		}
		lastDragTime = currentTime;
		
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
		
		// Stop any ongoing momentum animation
		if (momentumTimer != null && momentumTimer.isRunning()) {
			momentumTimer.stop();
		}
		
		p0 = getPoint(e);
		scrollPosition = viewPort.getViewPosition();
		dragging = true;
		lastDragTime = System.currentTimeMillis();
		velocityX = 0;
		velocityY = 0;
		oldCursor = canvas.getCursor();
		canvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}


	public void mouseReleased(MouseEvent e) {
		
		if (!buttonsMatch(e)) return;
		
		update(e);
		dragging = false;
		p0 = null;
		canvas.setCursor(oldCursor);
		
		// Start momentum animation if velocity is significant
		if (Math.abs(velocityX) > MIN_VELOCITY || Math.abs(velocityY) > MIN_VELOCITY) {
			startMomentumAnimation();
		}
	}

	private boolean buttonsMatch(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && buttons.contains(Buttons.LEFT)) return true;
		if (SwingUtilities.isMiddleMouseButton(e) && buttons.contains(Buttons.MIDDLE)) return true;
		if (SwingUtilities.isRightMouseButton(e) && buttons.contains(Buttons.RIGHT)) return true;
		return false;
	}
	
	private void startMomentumAnimation() {
		if (momentumTimer != null) {
			momentumTimer.stop();
		}
		
		momentumTimer = new Timer(MOMENTUM_TIMER_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Apply momentum scrolling
				Point currentPos = viewPort.getViewPosition();
				int newX = currentPos.x;
				int newY = currentPos.y;
				
				if (dragX && Math.abs(velocityX) > MIN_VELOCITY) {
					newX -= (int) (velocityX * MOMENTUM_TIMER_DELAY / 1000.0);
					velocityX *= MOMENTUM_DECAY;
				}
				
				if (dragY && Math.abs(velocityY) > MIN_VELOCITY) {
					newY -= (int) (velocityY * MOMENTUM_TIMER_DELAY / 1000.0);
					velocityY *= MOMENTUM_DECAY;
				}
				
				// Apply bounds checking similar to update() method
				if (dragX) {
					if (newX < 0) {
						newX = 0;
						velocityX = 0;
					} else if (canvas.getWidth() <= viewPort.getWidth()) {
						newX = 0;
						velocityX = 0;
					} else if (newX > canvas.getWidth() - viewPort.getWidth()) {
						newX = canvas.getWidth() - viewPort.getWidth();
						velocityX = 0;
					}
				}
				
				if (dragY) {
					if (newY < 0) {
						newY = 0;
						velocityY = 0;
					} else if (canvas.getHeight() <= viewPort.getHeight()) {
						newY = 0;
						velocityY = 0;
					} else if (newY > canvas.getHeight() - viewPort.getHeight()) {
						newY = canvas.getHeight() - viewPort.getHeight();
						velocityY = 0;
					}
				}
				
				viewPort.setViewPosition(new Point(newX, newY));
				
				// Stop animation if velocity is too low
				if (Math.abs(velocityX) <= MIN_VELOCITY && Math.abs(velocityY) <= MIN_VELOCITY) {
					momentumTimer.stop();
				}
			}
		});
		
		momentumTimer.start();
	}
	
}
