package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.peakaboo.framework.autodialog.model.style.layouts.FramedLayoutStyle;
import org.peakaboo.framework.stratus.api.Spacing;

public class FramesSwingLayout extends SimpleSwingLayout {
	
	@Override
	public JComponent getComponent() {
		FramedLayoutStyle style = (FramedLayoutStyle) group.getStyle();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(root, BorderLayout.CENTER);
		var border = new TitledBorder(group.getName());
		if (!style.isShowBorder()) { 
			border.setBorder(Spacing.bNone());
		}
		panel.setBorder(border);
		
		
		if (style.isHiddenOnDisable()) {
			panel.setVisible(group.isEnabled());
			group.getEnabledHook().addListener(e -> {
				panel.setVisible(e.booleanValue());
			});
		}
		return panel;
	}

}
