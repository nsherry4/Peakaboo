package org.peakaboo.framework.stratus.components.ui.layers;

import javax.swing.JComponent;
import javax.swing.JLayer;

public interface Layer {

	JLayer<JComponent> getJLayer();

	/**
	 * Returns the content component, usually the one supplied when the Layer was created
	 */
	JComponent getContent();

	/**
	 * Returns the outermost component for this layer, which may not be the same returned by getContent
	 */
	JComponent getOuterComponent();
	
	//clean up after we're done with this modal layer.
	void discard();
	
	/**
	 * Indicates if this layer is a modal layer
	 */
	boolean modal();
	
	/**
	 * Indicates the radius of the corners of this layer.
	 */
	//TODO: is this the best place for this?
	default int getCornerRadius() {
		return 0;
	}
	

}