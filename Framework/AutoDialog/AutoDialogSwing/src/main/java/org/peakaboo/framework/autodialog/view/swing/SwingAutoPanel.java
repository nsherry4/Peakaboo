package org.peakaboo.framework.autodialog.view.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.view.swing.layouts.SwingLayout;
import org.peakaboo.framework.autodialog.view.swing.layouts.SwingLayoutFactory;
import org.peakaboo.framework.stratus.api.Spacing;

public class SwingAutoPanel extends JPanel {

	public SwingAutoPanel(Group group) {
		this(group, true, null);
	}
	
	public SwingAutoPanel(Group group, boolean scrolled, SwingLayout layout) {
		setLayout(new BorderLayout());
		
		if (layout == null) {
			layout = SwingLayoutFactory.forGroup(group);
		}
		JComponent component = layout.getComponent();
		
		if (scrolled) {
			JScrollPane scroller = new JScrollPane(component);
			scroller.setBorder(Spacing.bNone());
			scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			this.add(scroller, BorderLayout.CENTER);
		} else {
			this.add(component, BorderLayout.CENTER);
		}
		
	}
	
	public SwingAutoPanel(Parameter<?> parameter) {
		this(new Group("", parameter));
		
	}
	
}
