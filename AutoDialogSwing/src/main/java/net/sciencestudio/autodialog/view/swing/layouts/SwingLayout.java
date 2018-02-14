package net.sciencestudio.autodialog.view.swing.layouts;


import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.view.layouts.Layout;
import net.sciencestudio.autodialog.view.swing.SwingView;

public interface SwingLayout extends Layout, SwingView {

	void initialize(Group group);
	void layout();
	
}
