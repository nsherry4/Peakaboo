package org.peakaboo.framework.swidget.live;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.peakaboo.framework.stratus.Stratus;

/**
 * Java's RepaintManager won't redraw a {@link Frame} in a Frame.ICONIFIED
 * state. This may have been acceptable behaviour in 2002, but it is very common
 * for Operating Systems to provide previews, overviews, or thumbnails of
 * minimized windows. LiveFrame detects calls from RepaintManager checking the
 * window state, and lies to it.
 * <br/><br/>
 * This class also listens to the frame's focus and sets a JComponent client
 * property on the root component indicating if the window is focused or not.
 * This is used by Stratus to paint the components differently depending on the
 * window focus.
 * 
 * @author NAS
 *
 */
public class LiveFrame extends JFrame {
	
	public LiveFrame() {
		super();
		init();
	}
	
	public LiveFrame(String title) {
		super(title);
		init();
	}

	public LiveFrame(GraphicsConfiguration gc) {
		super(gc);
		init();
	}
	
	public LiveFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
	}
	
	private void init() {
		addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				JRootPane root = getRootPane();
				root.putClientProperty(Stratus.KEY_WINDOW_FOCUSED, false);
				repaint();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				JRootPane root = getRootPane();
				root.putClientProperty(Stratus.KEY_WINDOW_FOCUSED, true);
				repaint();
			}
		});
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
