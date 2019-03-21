package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;

public class ScaleModeWidget extends JPanel {

	private ToggleImageButton relativeButton, absoluteButton;
	
	public ScaleModeWidget(MapFittingController viewController, String relative, String absolute, boolean warnRelative) {
		
		TitledBorder titleBorder = new TitledBorder("Scale By");
		titleBorder.setBorder(Spacing.bSmall());
		this.setBorder(titleBorder);
		this.setLayout(new BorderLayout());

		
		
		
		viewController.addListener(s -> {
			relativeButton.setSelected(viewController.getMapScaleMode() == MapScaleMode.RELATIVE);
			absoluteButton.setSelected(viewController.getMapScaleMode() == MapScaleMode.ABSOLUTE);
		});
		
		relativeButton = new ToggleImageButton(relative);
		absoluteButton = new ToggleImageButton(absolute);
		
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeButton);
		scaleGroup.add(absoluteButton);
		
		
		relativeButton.addActionListener(e -> viewController.setMapScaleMode(MapScaleMode.RELATIVE));
		absoluteButton.addActionListener(e -> viewController.setMapScaleMode(MapScaleMode.ABSOLUTE));
		
		relativeButton.setSelected(viewController.getMapScaleMode() == MapScaleMode.RELATIVE);
		absoluteButton.setSelected(viewController.getMapScaleMode() == MapScaleMode.ABSOLUTE);
		
		styleButton(relativeButton);
		styleButton(absoluteButton);
		
		ButtonBox box = new ButtonBox(0, false);
		box.addLeft(absoluteButton);
		box.addRight(relativeButton);
		
		if (warnRelative) {
			relativeButton.setToolTipText("This option gives qualitative results only");
			relativeButton.setBackground(new Color(0x00FFA000));
		}
		
		
		this.add(box, BorderLayout.CENTER);
		
	}
	
	private void styleButton(JToggleButton button) {
		button.setBorder(Spacing.bLarge());
		button.setPreferredSize(new Dimension(80, 32));
	}
	
}
