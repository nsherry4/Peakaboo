package net.sciencestudio.autodialog.view.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.view.swing.layouts.SwingLayoutFactory;
import swidget.widgets.Spacing;

public class SwingAutoPanel extends JPanel {

	Group group;
	
	public SwingAutoPanel(Group group) {
		this.group = group;
		setLayout(new BorderLayout());
		
		JScrollPane scroller = new JScrollPane(SwingLayoutFactory.forGroup(group).getComponent());
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		this.add(scroller, BorderLayout.CENTER);
		
	}
	
	public SwingAutoPanel(Parameter<?> parameter) {
		this(new Group("", parameter));
		
	}
	
}
