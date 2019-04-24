package org.peakaboo.framework.swidget.widgets.layerpanel;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

public interface Layer {

	JLayer<JComponent> getJLayer();

	JComponent getComponent();

	//clean up after we're done with this modal layer.
	void discard();
	
	boolean modal();

}