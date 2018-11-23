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
	

	public HeaderLayer(LayerPanel owner, boolean showClose) {
		this(owner, new JPanel(), showClose);
	}
	
	private HeaderLayer(LayerPanel owner, JPanel content, boolean showClose) {
		super(owner, content);	
		this.content = content;
		content.setLayout(new BorderLayout());
				
		//headerbox
		this.header = new HeaderBox();
		this.header.setShowClose(showClose);
		this.header.setOnClose(this::remove);
		content.add(header, BorderLayout.NORTH);
		
		
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		content.getInputMap(JComponent.WHEN_FOCUSED).put(key, key.toString());
		content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
		content.getActionMap().put(key.toString(), new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remove();
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

	@Override
	public void remove() {
		owner.removeLayer(this);
	}
	
	
}
