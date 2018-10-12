package peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import peakaboo.controller.mapper.settings.MapSettingsController;
import peakaboo.display.map.MapScaleMode;
import swidget.widgets.Spacing;
import swidget.widgets.layout.ButtonBox;

public class ScaleModeWidget extends JPanel {

	private JToggleButton relativeButton, absoluteButton;
	
	public ScaleModeWidget(MapSettingsController controller, String relative, String absolute, boolean warnRelative) {
		
		TitledBorder titleBorder = new TitledBorder("Scale By");
		titleBorder.setBorder(Spacing.bSmall());
		this.setBorder(titleBorder);
		this.setLayout(new BorderLayout());

		
		
		
		controller.addListener(s -> {
			relativeButton.setSelected(controller.getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE);
			absoluteButton.setSelected(controller.getMapFittings().getMapScaleMode() == MapScaleMode.ABSOLUTE);
		});
		
		relativeButton = new JToggleButton(relative);
		absoluteButton = new JToggleButton(absolute);
		
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeButton);
		scaleGroup.add(absoluteButton);
		
		
		relativeButton.addActionListener(e -> controller.getMapFittings().setMapScaleMode(MapScaleMode.RELATIVE));
		absoluteButton.addActionListener(e -> controller.getMapFittings().setMapScaleMode(MapScaleMode.ABSOLUTE));
		
		relativeButton.setSelected(controller.getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE);
		absoluteButton.setSelected(controller.getMapFittings().getMapScaleMode() == MapScaleMode.ABSOLUTE);
		
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
