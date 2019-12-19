package org.peakaboo.framework.swidget.live;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JRootPane;

import org.peakaboo.framework.stratus.Stratus;

/**
 * This class listens to the dialog's focus and sets a JComponent client
 * property on the root component indicating if the window is focused or not.
 * This is used by Stratus to paint the components differently depending on the
 * window focus.
 * 
 * @author NAS
 *
 */

public class LiveDialog extends JDialog {

	public LiveDialog() {
		super();
		init();
	}
	
	public LiveDialog(Window owner, String string, ModalityType documentModal) {
		super(owner, string, documentModal);
		init();
	}

	public LiveDialog(Window parent) {
		super(parent);
		init();
	}

	public LiveDialog(Window owner, String title) {
		super(owner, title);
		init();
	}

	public LiveDialog(JDialog owner, String string, boolean b) {
		super(owner, string, b);
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
	
}
