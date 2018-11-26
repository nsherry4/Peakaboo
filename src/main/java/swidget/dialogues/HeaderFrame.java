package swidget.dialogues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import swidget.widgets.ClearPanel;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layout.HeaderBox;

public class HeaderFrame extends JFrame {

	private LayerPanel root;
	private JComponent content;
	
	private HeaderBox header;
	private Component body;
	
	private Runnable onClose;
	
	public HeaderFrame() {
		this(() -> {});
	}
	
	public HeaderFrame(Runnable onClose) {
		this.onClose = onClose;
		
		this.setUndecorated(true);
		
		root = new LayerPanel();
		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		root.setBorder(new MatteBorder(1, 1, 1, 1, border));
		this.setContentPane(root);
		
		content = root.getContentLayer();
		content.setLayout(new BorderLayout());
		
		
		header = new HeaderBox();
		header.setShowClose(true);
		header.setOnClose(() -> {
			this.setVisible(false);
			this.onClose.run();
		});
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
		
		pack();
	}

	public LayerPanel getLayerRoot() {
		return root;
	}
	
	
	
	
}
