package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;

public class HeaderLayer extends ModalLayer {

	protected HeaderPanel root;
	private Runnable onClose;
	private JScrollPane scroller;

	public HeaderLayer(LayerPanel owner, boolean showClose) {
		this(owner, showClose, false);
	}
	
	public HeaderLayer(LayerPanel owner, boolean showClose, boolean sizeWithOwner) {
		super(owner, new HeaderPanel(), sizeWithOwner);	
		root = (HeaderPanel) super.getComponent();
		
		//headerbox
		root.getHeader().setShowClose(showClose);
		root.getHeader().setOnClose(this::remove);
				
		wireEscapeClose();
		
	}
	
	public HeaderLayer(LayerPanel owner, HeaderBox header, Component body) {
		this(owner, header, body, false);
	}
	
	public HeaderLayer(LayerPanel owner, HeaderBox header, Component body, boolean sizeWithOwner) {
		super(owner, new HeaderPanel(header, body), sizeWithOwner);	
		root = (HeaderPanel) super.getComponent();
		
		root.getHeader().setOnClose(this::remove);
		wireEscapeClose();
	}
	
	private void wireEscapeClose() {
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

	public void setBody(Component body) {	
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



