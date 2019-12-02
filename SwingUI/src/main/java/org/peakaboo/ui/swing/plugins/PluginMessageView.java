package org.peakaboo.ui.swing.plugins;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layout.CenteringLayout;

public class PluginMessageView extends JPanel {

	public PluginMessageView(String message) {
		setBorder(Spacing.bHuge());
		
		JLabel label = new JLabel();
		label.setText(Swidget.lineWrapHTML(label, message, 350));
		label.setForeground(Color.GRAY);
		
		setLayout(new CenteringLayout());
		add(label);
	}
	
}
