package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;

public class TabbedSwingLayout extends AbstractSwingLayout {

	public void layout() {
		root.removeAll();
		JTabbedPane tabs = new JTabbedPane();
		root.setLayout(new BorderLayout());
		
		
		List<Value<?>> generalList = new ArrayList<>();		
		for (Value<?> value : group.getValue()) {
			if (value instanceof Parameter) {
				generalList.add(value);
			}
		}
		Group general = new Group("General", generalList);
		
		if (!general.getValue().isEmpty()) {
			tabs.addTab("General", null, SwingLayoutFactory.forGroup(general).getComponent(), null);
		}
		for (Value<?> value : group.getValue()) {
			if (value instanceof Group g) {
				tabs.addTab(value.getName(), null, SwingLayoutFactory.forGroup(g).getComponent(), null);
			}
		}
		
		root.add(tabs, BorderLayout.CENTER);
		
	}

	
}
