package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderPanel;

public class HeaderLayer extends ModalLayer {

	private HeaderPanel root;
	private Runnable onClose;

	public HeaderLayer(LayerPanel owner, boolean showClose) {
		this(owner, showClose, false);
	}
	
	public HeaderLayer(LayerPanel owner, boolean showClose, boolean sizeWithOwner) {
		super(owner, new HeaderPanel(), sizeWithOwner);	
		root = (HeaderPanel) super.getComponent();
		
		//headerbox
		root.getHeader().setShowClose(showClose);
		root.getHeader().setOnClose(this::remove);
				
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		root.getInputMap(JComponent.WHEN_FOCUSED).put(key, key.toString());
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
		root.getActionMap().put(key.toString(), new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		
	}

	
	public JComponent getContentRoot() {
		return root.getContentLayer();
	}

	public HeaderBox getHeader() {
		return root.getHeader();
	}

	public Component getBody() {
		return root.getBody();
	}

	public void setBody(JComponent body) {
		root.setBody(body);
	}

	@Override
	public void remove() {
		owner.removeLayer(this);
		if (onClose != null) {
			onClose.run();
		}
	}


	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
	
	
}
