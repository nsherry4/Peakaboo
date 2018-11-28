package swidget.widgets.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import swidget.widgets.Spacing;
import swidget.widgets.layerpanel.LayerPanel;

public class HeaderPanel extends LayerPanel {

	private JComponent content;
	
	private HeaderBox header;
	private Component body;	
		
	public HeaderPanel() {
		
		content = getContentLayer();
		content.setLayout(new GridBagLayout());
		
		
		header = new HeaderBox();
		content.add(header, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Spacing.iNone(), 0, 0));

		setBody(new JPanel());	
	}
	
	
	public HeaderBox getHeader() {
		return header;
	}


	public Component getBody() {
		return body;
	}
	
	public void setBody(Component body) {
		if (this.body != null) {
			this.content.remove(this.body);
		}
		
		this.body = body;
		
		if (this.body != null) {
			this.content.add(this.body, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, Spacing.iNone(), 0, 0));
		}
	}

	
}
