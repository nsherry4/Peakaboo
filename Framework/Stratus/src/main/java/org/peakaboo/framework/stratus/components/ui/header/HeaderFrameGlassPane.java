package org.peakaboo.framework.stratus.components.ui.header;


import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener; 
 
//Derived from:
/* 
 * GlassPane tutorial 
 * "A well-behaved GlassPane" 
 * http://weblogs.java.net/blog/alexfromsun/ 
 * <p/>
 * This is a much better version of GlassPane 
 * because it is really transparent for MouseEvents,  
 * the only drawback that it doesn't update the mouse's cursor 
 * for the components underneath  
 * (e.g. doesn't show TEXT_CURSOR for textComponents) 
 *  
 * @author Alexander Potochkin 
 */  

class HeaderFrameGlassPane extends JPanel implements AWTEventListener, MouseMotionListener, MouseInputListener, MouseWheelListener { 

	private Window window;
	private boolean dragging = false;
	
	private final static int RESIZE_BORDER = 8;
	
	private boolean resizing = false;
	private boolean resizeNorth, resizeSouth, resizeWest, resizeEast;
	private Point resizeCursor = null;
	private Point resizePosition = null;
	private Dimension resizeDimensions = null;
	
	
    public HeaderFrameGlassPane(Window window) { 
    	super(); 
    	this.window = window;
    	setOpaque(false); 
    } 
   
	@Override
	public void eventDispatched(AWTEvent event) {
		Object oSource = event.getSource();
		if (!(oSource instanceof Component)) { return; }
		//Component component
		Component source = (Component) oSource;
		
		Window sourceWindow;
		if (source instanceof Window) {
			sourceWindow = (Window) source;
		} else {
			sourceWindow = SwingUtilities.getWindowAncestor(source);
		}
		if (!window.equals(sourceWindow)) { return; }
		
//		System.out.println(event);
//		System.out.println(event.getSource());
//		System.out.println("-----------------");
		
		if (event instanceof MouseWheelEvent) {
			dispatchWheelEvent((MouseWheelEvent)event);
		} else if (event instanceof MouseEvent) {
			dispatchEvent((MouseEvent)event);
		}
		
	}
	

	private void dispatchEvent(MouseEvent e) {
		
		int id = e.getID();
		switch (id) {
		case MouseEvent.MOUSE_PRESSED:
			mousePressed(e);
			break;
		case MouseEvent.MOUSE_RELEASED:
			mouseReleased(e);
			break;
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked(e);
			break;
		case MouseEvent.MOUSE_EXITED:
			mouseExited(e);
			break;
		case MouseEvent.MOUSE_ENTERED:
			mouseEntered(e);
			break;
		case MouseEvent.MOUSE_MOVED:
			mouseMoved(e);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			mouseDragged(e);
			break;
		}
		
	}

	private void dispatchWheelEvent(MouseWheelEvent e) {
		int id = e.getID();
		switch (id) {
		case MouseEvent.MOUSE_WHEEL:
			mouseWheelMoved(e);
			break;
		}
	}
    
    /** 
     * If someone adds a mouseListener to the GlassPane or set a new cursor 
     * we expect that he knows what he is doing 
     * and return the super.contains(x, y) 
     * otherwise we return false to respect the cursors 
     * for the underneath components 
     */ 
    @Override
    public boolean contains(int x, int y) {
    	if (isEdge(x, y)) return true;
        if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0 
                && getMouseWheelListeners().length == 0 
                && getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) { 
            return false; 
        }
        return super.contains(x, y); 
    } 
    
    
    private MouseEvent frameEvent(MouseEvent e) {
		return SwingUtilities.convertMouseEvent((Component)e.getSource(), e, window);
	}
    
	
	private boolean isSouth(int x, int y) {
		int height = window.getHeight();
		return (y > height-RESIZE_BORDER && y < height+RESIZE_BORDER);
	}
	
	private boolean isNorth(int x, int y) {
		return (y > -RESIZE_BORDER && y < +RESIZE_BORDER);
	}
	
	private boolean isWest(int x, int y) {
		return (x > -RESIZE_BORDER && x < +RESIZE_BORDER);
	}
	
	private boolean isEast(int x, int y) {
		int width = window.getWidth();
		return (x > width-RESIZE_BORDER && x < width+RESIZE_BORDER);
	}
	
	private boolean isEdge(int x, int y) {
		return isNorth(x, y) || isSouth(x, y) || isWest(x, y) || isEast(x, y);
	}
	
	private boolean isEdge(MouseEvent e) {
		return isEdge(e.getX(), e.getY());
	}
	
	private Cursor getCursor(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		boolean north = isNorth(x, y);
		boolean south = isSouth(x, y);
		boolean west = isWest(x, y);
		boolean east = isEast(x, y);
		
		if (north && west) return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		if (north && east) return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		if (south && east) return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		if (south && west) return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		
		if (north) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		if (east)  return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		if (south) return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		if (west)  return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		
		return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}
	
	private void resizeFrame(MouseEvent e) {
		Point current = e.getLocationOnScreen();
		
		int dx = current.x - resizeCursor.x;
		int dy = current.y - resizeCursor.y;
		
		int fx = resizePosition.x;
		int fy = resizePosition.y;
		int fw = resizeDimensions.width;
		int fh = resizeDimensions.height;
		
		if (resizeNorth) {
			fh -= dy;
			fy += dy;
		}
		if (resizeSouth) {
			fh += dy;
		}
		if (resizeEast) {
			fw += dx;			
		}
		if (resizeWest) {
			fw -= dx;
			fx += dx;
		}
		
		window.setLocation(fx, fy);
		window.setSize(fw, fh);
	
	}
	
	
	@Override
	public void mouseDragged(MouseEvent raw) {
		MouseEvent e = frameEvent(raw);
		if (dragging) { return; }
		if (resizing) { 
			raw.consume();
			resizeFrame(e);
			return;
		}
		setCursor(getCursor(e));
	}

	@Override
	public void mouseMoved(MouseEvent raw) {
		MouseEvent e = frameEvent(raw);
		setCursor(getCursor(e));
		if (isEdge(e)) { raw.consume(); }
	}

	
	
	
	@Override
	public void mouseClicked(MouseEvent raw) {
		MouseEvent e = frameEvent(raw);
		if (resizing) { raw.consume(); }
		if (isEdge(e)) { raw.consume(); }
	}

	@Override
	public void mousePressed(MouseEvent raw) {
		MouseEvent e = frameEvent(raw);
		if (isEdge(e)) { raw.consume(); }
		if (isEdge(e) && !dragging) {
			resizing = true;
			int ex = e.getX();
			int ey = e.getY();
					
			resizeNorth = isNorth(ex, ey);
			resizeSouth = isSouth(ex, ey);
			resizeWest = isWest(ex, ey);
			resizeEast = isEast(ex, ey);
			resizeCursor = e.getLocationOnScreen();
			resizePosition = window.getLocation();
			resizeDimensions = window.getSize();
		} else {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent raw) {
		MouseEvent e = frameEvent(raw);
		if (resizing) { raw.consume(); }
		if (isEdge(e)) { raw.consume(); }
		resizing = false;
		resizeNorth = false;
		resizeSouth = false;
		resizeWest = false;
		resizeEast = false;
		resizeCursor = null;
		resizePosition = null;
		resizeDimensions = null;
		dragging = false;
	}

	@Override
	public void mouseEntered(MouseEvent raw) {}

	@Override
	public void mouseExited(MouseEvent raw) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent raw) {
		MouseEvent e = frameEvent(raw);
		if (isEdge(e)) { raw.consume(); }
	}
} 

