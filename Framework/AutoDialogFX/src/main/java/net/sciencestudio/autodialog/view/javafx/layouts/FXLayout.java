package net.sciencestudio.autodialog.view.javafx.layouts;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.view.javafx.FXView;
import net.sciencestudio.autodialog.view.layouts.Layout;

public interface FXLayout extends Layout, FXView {

	void initialize(Group group);
	void layout();
	
}
