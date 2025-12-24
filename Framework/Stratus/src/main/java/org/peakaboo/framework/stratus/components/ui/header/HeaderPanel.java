package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

public class HeaderPanel extends LayerPanel {

	private JComponent content;
	
	private HeaderBox header;
	private Component body;	
		
	public HeaderPanel() {
		super(false);
		header = new HeaderBox();
		
		content = getContentLayer();
		content.setLayout(new GridBagLayout());
		content.add(header, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Spacing.iNone(), 0, 0));

		setBody(new JPanel());
	}
	
	
	public HeaderPanel(HeaderBox header, Component body) {
		super(false);
		this.header = header;
		
		content = getContentLayer();
		content.setLayout(new GridBagLayout());
		content.add(header, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Spacing.iNone(), 0, 0));
		
		setBody(body);
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

		// Recalculate layout and repaint after component changes
		this.content.revalidate();
		this.content.repaint();
	}

	
}
