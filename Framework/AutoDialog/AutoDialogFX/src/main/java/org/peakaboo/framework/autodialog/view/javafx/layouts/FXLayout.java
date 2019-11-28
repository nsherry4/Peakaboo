package org.peakaboo.framework.autodialog.view.javafx.layouts;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.javafx.FXView;
import org.peakaboo.framework.autodialog.view.layouts.Layout;

public interface FXLayout extends Layout, FXView {

	void initialize(Group group);
	void layout();
	
}
