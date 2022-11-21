package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.border.MatteBorder;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.live.LiveFrame;

public class HeaderFrame extends LiveFrame {

	private HeaderPanel root;
	private Runnable onClose;
	private HeaderFrameGlassPane glass;
	
	public HeaderFrame() {
		this(() -> {});
	}
	
	public HeaderFrame(Runnable onClose) {
		this.onClose = onClose;
		
		this.setUndecorated(true);
		
		root = new HeaderPanel();
		root.setBorder(new MatteBorder(1, 1, 1, 1, Stratus.getTheme().getWidgetBorder()));
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
