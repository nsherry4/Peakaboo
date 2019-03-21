package net.sciencestudio.autodialog.view.swing.layouts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.Value;

public class TabbedSwingLayout extends AbstractSwingLayout {

	
	private JTabbedPane tabs;
	
	public void layout() {
		root.removeAll();
		tabs = new JTabbedPane();
		root.setLayout(new BorderLayout());
		
		
		List<Value<?>> generalList = new ArrayList<>();		
		for (Value<?> value : group.getValue()) {
			if (value instanceof Parameter) {
				generalList.add(value);
			}
		}
		Group general = new Group("General", generalList);
		
		if (general.getValue().size() > 0) {
			tabs.addTab("General", null, SwingLayoutFactory.forGroup(general).getComponent(), null);
		}
		for (Value<?> value : group.getValue()) {
			if (value instanceof Group) {
				tabs.addTab(value.getName(), null, SwingLayoutFactory.forGroup((Group)value).getComponent(), null);
			}
		}
		
		root.add(tabs, BorderLayout.CENTER);
		
	}







	
}
