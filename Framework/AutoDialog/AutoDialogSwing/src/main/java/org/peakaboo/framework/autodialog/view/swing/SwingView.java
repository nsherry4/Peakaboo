package org.peakaboo.framework.autodialog.view.swing;

import javax.swing.JComponent;

import org.peakaboo.framework.autodialog.view.View;

public interface SwingView extends View {

	/**
	 * Returns the graphical interface component for this editor.
	 * @return
	 */
	JComponent getComponent();
	
}
