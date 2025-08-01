package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.AWTEvent;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

public class ContentLayer implements Layer {

	private JPanel contentPanel = new JPanel();
	private final JLayer<JComponent> contentJLayer;
	
	public ContentLayer(LayerPanel parent, boolean topFade) {
		//set blur in case another layer is placed on top of this one
		LayerBlurUI<JComponent> blurUI = new LayerBlurUI<JComponent>(parent, contentPanel) {
			@Override
			public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l) {
				((InputEvent) e).consume();
			}
		};
		blurUI.setTopFade(topFade);
		contentJLayer = new JLayer<JComponent>(contentPanel, blurUI);
	}
	
	@Override
	public JLayer<JComponent> getJLayer() {
		return contentJLayer;
	}

	@Override
	public JPanel getContent() {
		return contentPanel;
	}
	
	@Override
	public JComponent getOuterComponent() {
		return getContent();
	}

	@Override
	public void discard() {}

	@Override
	public boolean modal() {
		return true;
	}

}
