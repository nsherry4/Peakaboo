package swidget.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class CenteringPanel extends JPanel {
	
	public CenteringPanel(JComponent component) {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0f;
		c.weighty = 0f;
		
		add(component, c);
		
	}
	
}
