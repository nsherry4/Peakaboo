package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderPanel;

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
	
	//Don't wrap the whole thing in a JScrollPane like our parent class would, we provide our own
	@Override
	protected Component wrapComponent(Component component) {
		return component;
	}
	
	//Provide parent class the part of our UI that is scrolled
	@Override
	protected JScrollPane getScroller(Component component) {
		return scroller;
	}

	//Inform parent class how much space is unscrolled
	@Override
	protected Dimension getNonScrolledSize() {
		return new Dimension(0, root.getHeader().getPreferredSize().height); //?
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
		scroller = scrolled(body);
		root.setBody(scroller);
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



