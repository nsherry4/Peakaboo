package org.peakaboo.framework.autodialog.view.javafx.layouts;

import org.peakaboo.framework.autodialog.view.javafx.FXView;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

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
