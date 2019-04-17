package org.peakaboo.framework.swidget.dialogues;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderPanel;

public class HeaderDialog extends JDialog {


	private HeaderPanel root;
	private Runnable onClose;
	private HeaderFrameGlassPane glass;
	
	public HeaderDialog(Window parent) {
		this(parent, () -> {});
	}
	
	public HeaderDialog(Window parent, Runnable onClose) {
		super(parent);
		this.onClose = onClose;
		
		this.setUndecorated(true);
		
		root = new HeaderPanel();
		root.setBorder(new MatteBorder(1, 1, 1, 1, Swidget.dividerColor()));
		this.setContentPane(root);
		
		
		root.getHeader().setShowClose(true);
		root.getHeader().setOnClose(() -> {
			this.setVisible(false);
			Toolkit.getDefaultToolkit().removeAWTEventListener(glass);
			this.onClose.run();
		});

		HeaderFrameGlassPane glass = new HeaderFrameGlassPane(this);
		this.setGlassPane(glass);
		glass.setVisible(true);
				
		Toolkit.getDefaultToolkit().addAWTEventListener(glass, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		
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
