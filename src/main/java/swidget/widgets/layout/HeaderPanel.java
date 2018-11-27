package swidget.widgets.layout;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import swidget.widgets.layerpanel.LayerPanel;

public class HeaderPanel extends LayerPanel {

	private JComponent content;
	
	private HeaderBox header;
	private Component body;	
	
	public HeaderPanel() {
		
		content = getContentLayer();
		content.setLayout(new BorderLayout());
		
		
		header = new HeaderBox();
		content.add(header, BorderLayout.NORTH);
		
		body = new JPanel();
		content.add(body, BorderLayout.CENTER);
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
			this.content.add(this.body, BorderLayout.CENTER);
		}
	}

	
}
