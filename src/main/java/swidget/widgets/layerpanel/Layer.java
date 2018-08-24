package swidget.widgets.layerpanel;

import javax.swing.JLayer;
import javax.swing.JPanel;

public interface Layer {

	JLayer<JPanel> getJLayer();

	JPanel getComponent();

	//clean up after we're done with this modal layer.
	void discard();
	
	boolean modal();

}