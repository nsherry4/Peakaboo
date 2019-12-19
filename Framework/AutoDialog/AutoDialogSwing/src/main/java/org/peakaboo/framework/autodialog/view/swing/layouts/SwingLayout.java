package org.peakaboo.framework.autodialog.view.swing.layouts;


import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.layouts.Layout;
import org.peakaboo.framework.autodialog.view.swing.SwingView;

public interface SwingLayout extends Layout, SwingView {

	void initialize(Group group);
	void layout();
	
}
