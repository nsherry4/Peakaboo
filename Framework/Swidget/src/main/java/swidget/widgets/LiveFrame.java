package swidget.widgets;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

/**
 * Java's RepaintManager won't redraw a {@link Frame} in a Frame.ICONIFIED
 * state. This may have been acceptable behaviour in 2002, but it is very common
 * for Operating Systems to provide previews, overviews, or thumbnails of
 * minimized windows. LiveFrame detects calls from RepaintManager checking the
 * window state, and lies to it.
 * 
 * @author NAS
 *
 */
public class LiveFrame extends JFrame {
	
	public LiveFrame() {
		super();
	}
	
	public LiveFrame(String title) {
		super(title);
	}

	public LiveFrame(GraphicsConfiguration gc) {
		super(gc);
	}
	
	public LiveFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}
	
	@Override
	public int getExtendedState() {
		
		StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stes) {
			//if the RepaintManager is calling and and we're minimized, lie to it so that it will still repaint the window
			if ((ste.getClassName().equals("javax.swing.RepaintManager") && (super.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED)) {
				return super.getExtendedState() - Frame.ICONIFIED;
			}
		}
		
		return super.getExtendedState();
	}

}
