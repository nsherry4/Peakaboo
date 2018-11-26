package swidget.dialogues;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderPanel;

public class HeaderFrame extends JFrame {

	private HeaderPanel root;
	private Runnable onClose;
	
	public HeaderFrame() {
		this(() -> {});
	}
	
	public HeaderFrame(Runnable onClose) {
		this.onClose = onClose;
		
		this.setUndecorated(true);
		
		root = new HeaderPanel();
		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		root.setBorder(new MatteBorder(1, 1, 1, 1, border));
		this.setContentPane(root);
		
		
		root.getHeader().setShowClose(true);
		root.getHeader().setOnClose(() -> {
			this.setVisible(false);
			this.onClose.run();
		});

	}


	public HeaderBox getHeader() {
		return root.getHeader();
	}


	public Component getBody() {
		return root.getBody();
	}
	
	public void setBody(Component body) {
		root.setBody(body);
		pack();
	}

	public LayerPanel getLayerRoot() {
		return root;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	
}
