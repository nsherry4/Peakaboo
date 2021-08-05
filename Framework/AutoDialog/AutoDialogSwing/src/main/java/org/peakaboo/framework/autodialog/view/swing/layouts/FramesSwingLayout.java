package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.peakaboo.framework.autodialog.model.style.layouts.FramedLayoutStyle;
import org.peakaboo.framework.autodialog.view.swing.SwingView;

public class FramesSwingLayout extends SimpleSwingLayout {

	@Override
	protected JComponent component(SwingView view) {
		return view.getComponent();
	}
	
	@Override
	public JComponent getComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(root, BorderLayout.CENTER);
		panel.setBorder(new TitledBorder(group.getName()));
		
		FramedLayoutStyle style = (FramedLayoutStyle) group.getStyle();
		if (style.isHiddenOnDisable()) {
			panel.setVisible(group.isEnabled());
			group.getEnabledHook().addListener(e -> {
				panel.setVisible(e.booleanValue());
			});
		}
		return panel;
	}

}
