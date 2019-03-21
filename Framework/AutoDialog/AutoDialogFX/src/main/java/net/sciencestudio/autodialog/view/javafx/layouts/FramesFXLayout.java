package net.sciencestudio.autodialog.view.javafx.layouts;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import net.sciencestudio.autodialog.view.javafx.FXView;

public class FramesFXLayout extends SimpleFXLayout {

	
	protected Node component(FXView view) {
		if (view instanceof FXLayout) {
			//this is a layout (of a group). Put it in a frame
			TitledPane pane = new TitledPane(view.getTitle(), view.getComponent());
			return pane;			
		} else {
			return view.getComponent();
		}
	}

}
