package org.peakaboo.ui.swing.mapping.sidebar.modes;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.controller.mapper.settings.MapSettingsController;

import swidget.widgets.Spacing;

public class OverlayLowCutoffSlider extends JPanel {

	public OverlayLowCutoffSlider(MapSettingsController controller) {
		setLayout(new BorderLayout());
		setBorder(Spacing.bMedium());
		JSlider slider = new JSlider(0, 100, 0);
		JLabel label = new JLabel("Low Signal Cutoff - " + (int)(controller.getView().getOverlayLowCutoff() * 100) + "%");
		this.add(slider, BorderLayout.CENTER);
		this.add(label, BorderLayout.NORTH);
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.getView().setOverlayLowCutoff(((float)slider.getValue()) / 100f);
			}
		});
		
		controller.addListener(t -> {
			if (UpdateType.UI_OPTIONS.toString().equals(t)) {
				slider.setValue((int)(controller.getView().getOverlayLowCutoff() * 100));
				label.setText("Low Signal Cutoff - " + (int)(controller.getView().getOverlayLowCutoff() * 100) + "%");
			}
		});
		
	}
	
}
