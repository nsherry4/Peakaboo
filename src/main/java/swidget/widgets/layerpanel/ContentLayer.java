package swidget.widgets.layerpanel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.InputEvent;

import javax.swing.JLayer;
import javax.swing.JPanel;

public class ContentLayer implements Layer {

	private JPanel contentPanel = new JPanel();
	private final JLayer<JPanel> contentJLayer;
	
	public ContentLayer(LayerPanel parent) {
		//set blur in case another layer is placed on top of this one
		LayerBlurUI<JPanel> blurUI = new LayerBlurUI<JPanel>(parent, contentPanel) {
			@Override
			public void eventDispatched(AWTEvent e, JLayer<? extends JPanel> l) {
				((InputEvent) e).consume();
			}
		};
		contentJLayer = new JLayer<JPanel>(contentPanel, blurUI);
	}
	
	@Override
	public JLayer<JPanel> getJLayer() {
		return contentJLayer;
	}

	@Override
	public JPanel getComponent() {
		return contentPanel;
	}

	@Override
	public void discard() {}

	@Override
	public boolean modal() {
		return true;
	}

}
