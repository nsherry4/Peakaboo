package swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import swidget.Swidget;


public class ComponentListPanel extends ClearPanel {

	public ComponentListPanel(List<? extends Component> components) {
	
		ClearPanel content = new ClearPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(layout);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = Spacing.iSmall();
		gbc.fill = GridBagConstraints.HORIZONTAL;
							

		for (Component component : components) {
			try {
				content.add(component, gbc);
				gbc.gridy++;
			} catch (Exception ex) {
				Swidget.logger().log(Level.SEVERE, "Failed to construct ComponentListPanel", ex);
			}
		}
		
		
		JScrollPane scroller = new JScrollPane(content);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.getVerticalScrollBar().setUnitIncrement(10);
		scroller.getVerticalScrollBar().setUnitIncrement(50);
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		
		this.setLayout(new BorderLayout());
		this.add(scroller, BorderLayout.CENTER);
		
		
	}
	
}
