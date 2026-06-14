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

/**
 * A modal overlay layer with a {@link HeaderPanel}, displayed within an existing window.
 * <p>
 * HeaderLayer provides an in-window modal overlay (not a separate window) with a custom header
 * bar. Unlike {@link HeaderDialog} and {@link HeaderFrame}, which create new OS windows,
 * HeaderLayer draws over an existing {@link LayerPanel} within the same window.
 * </p>
 * <p>
 * <strong>Features:</strong>
 * </p>
 * <ul>
 * <li>Modal overlay that blocks interaction with underlying components</li>
 * <li>Custom header bar with optional close button</li>
 * <li>ESC key automatically closes the layer</li>
 * <li>Optional auto-sizing to match owner dimensions</li>
 * <li>No separate window - rendered within parent LayerPanel</li>
 * </ul>
 * <p>
 * <strong>When to Use:</strong>
 * </p>
 * <ul>
 * <li>Use HeaderLayer for in-window modal overlays (dialogs within the application)</li>
 * <li>Use {@link HeaderDialog} for separate modal dialog windows</li>
 * <li>Use {@link HeaderFrame} for separate non-modal windows</li>
 * </ul>
 * <p>
 * <strong>Example:</strong>
 * </p>
 * <pre>
 * HeaderLayer layer = new HeaderLayer(parentPanel, true);
 * layer.getHeader().setCentre("Settings");
 * layer.setBody(settingsPanel);
 * parentPanel.pushLayer(layer);
 * </pre>
 *
 * @see HeaderDialog
 * @see HeaderFrame
 * @see HeaderPanel
 * @see ModalLayer
 */
public class HeaderLayer extends ModalLayer {

	protected HeaderPanel root;
	private Runnable onClose;
	private JScrollPane scroller;

	public HeaderLayer(LayerPanel owner, boolean showClose) {
		this(owner, showClose, false);
	}
	
	public HeaderLayer(LayerPanel owner, boolean showClose, boolean sizeWithOwner) {
		super(owner, new HeaderPanel(), sizeWithOwner);	
		root = (HeaderPanel) super.getContent();
		
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
		root = (HeaderPanel) super.getContent();
		
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
	
	/**
	 * Sets whether the header casts a soft shadow onto the body. Enabled by default.
	 *
	 * @param headerShadow true to draw the shadow, false to hide it
	 */
	public void setHeaderShadow(boolean headerShadow) {
		this.root.setHeaderShadow(headerShadow);
	}
	
	/**
	 * Gets whether the header casts a soft shadow onto the body.
	 *
	 * @return true if the shadow is drawn, false otherwise
	 */
	public boolean isHeaderShadow() {
		return this.root.isHeaderShadow();
	}
	
	/**
	 * Fluent variant of {@link #setHeaderShadow(boolean)}.
	 *
	 * @param headerShadow true to draw the shadow, false to hide it
	 * @return this HeaderLayer
	 */
	public HeaderLayer withHeaderShadow(boolean headerShadow) {
		setHeaderShadow(headerShadow);
		return this;
	}
	
	/**
	 * Sets whether the header paints its background. When disabled, the header is
	 * transparent and shows whatever lies behind it. Enabled by default.
	 *
	 * @param paintBackground true to paint the background, false to leave it transparent
	 */
	public void setHeaderBackgroundPainted(boolean paintBackground) {
		this.root.setHeaderBackgroundPainted(paintBackground);
	}
	
	/**
	 * Gets whether the header paints its background.
	 *
	 * @return true if the background is painted, false otherwise
	 */
	public boolean isHeaderBackgroundPainted() {
		return this.root.isHeaderBackgroundPainted();
	}
	
	/**
	 * Fluent variant of {@link #setHeaderBackgroundPainted(boolean)}.
	 *
	 * @param paintBackground true to paint the background, false to leave it transparent
	 * @return this HeaderLayer
	 */
	public HeaderLayer withHeaderBackgroundPainted(boolean paintBackground) {
		setHeaderBackgroundPainted(paintBackground);
		return this;
	}
	
}



