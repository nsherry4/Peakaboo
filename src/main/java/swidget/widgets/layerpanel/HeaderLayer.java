package swidget.widgets.layerpanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layout.HeaderBox;

public class HeaderLayer extends ModalLayer {

	private JPanel content;
	
	private JComponent body;
	private HeaderBox header;
	private Runnable onClose;
	
	public HeaderLayer(LayerPanel owner) {
		this(owner, () -> {});
	}
	
	public HeaderLayer(LayerPanel owner, Runnable onClose) {
		this(owner, onClose, new JPanel());
	}
	
	private HeaderLayer(LayerPanel owner,  Runnable onClose, JPanel content) {
		super(owner, content);	
		this.content = content;
		this.onClose = onClose;
		content.setLayout(new BorderLayout());
				
		//headerbox
		this.header = new HeaderBox();
		ImageButton close = HeaderBox.closeButton().withAction(this::removeLayer);
		header.setRight(close);
		content.add(header, BorderLayout.NORTH);
		
		
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		content.getInputMap(JComponent.WHEN_FOCUSED).put(key, key.toString());
		content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
		content.getActionMap().put(key.toString(), new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeLayer();
			}
		});
		
	}
	
	public JComponent getContentRoot() {
		return content;
	}

	public HeaderBox getHeader() {
		return header;
	}

	public JComponent getBody() {
		return body;
	}

	public void setBody(JComponent body) {
		content.remove(body);
		this.body = body;
		if (body != null) {
			content.add(body, BorderLayout.CENTER);
		}
	}

	
	
	public Runnable getOnClose() {
		return onClose;
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	@Override
	protected void removeLayer() {
		owner.removeLayer(this);
		if (onClose != null) {
			onClose.run();
		}
	}
	
	
}
