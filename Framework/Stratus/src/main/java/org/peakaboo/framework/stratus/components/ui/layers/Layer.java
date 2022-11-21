package org.peakaboo.framework.stratus.components.ui.layers;

import javax.swing.JComponent;
import javax.swing.JLayer;

public interface Layer {

	JLayer<JComponent> getJLayer();

	JComponent getComponent();

	//clean up after we're done with this modal layer.
	void discard();
	
	boolean modal();

}