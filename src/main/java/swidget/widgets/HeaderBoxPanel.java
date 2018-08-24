package swidget.widgets;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class HeaderBoxPanel extends JPanel {

	public HeaderBoxPanel(HeaderBox header, JComponent component) {
		super(new BorderLayout());
		add(header, BorderLayout.NORTH);
		add(component, BorderLayout.CENTER);
	}
	
}
