package net.sciencestudio.autodialog.view.swing.layouts;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.sciencestudio.autodialog.view.swing.SwingView;

public class FramesSwingLayout extends SimpleSwingLayout {

	
	protected JComponent component(SwingView view) {
		if (view instanceof SwingLayout) {
			//this is a layout (of a group). Put it in a frame
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(view.getComponent(), BorderLayout.CENTER);
			panel.setBorder(new TitledBorder(view.getTitle()));
			return panel;			
		} else {
			return view.getComponent();
		}
	}

}
