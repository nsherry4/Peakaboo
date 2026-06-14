package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.BodyShadowPanel;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

/**
 * A LayerPanel that combines a {@link HeaderBox} with a body component.
 * <p>
 * HeaderPanel arranges a header bar at the top with an arbitrary body component below it,
 * creating a common UI pattern used throughout the Stratus framework. The header remains
 * fixed at the top while the body fills the remaining space.
 * </p>
 * <p>
 * <strong>Usage:</strong>
 * </p>
 * <pre>
 * HeaderPanel panel = new HeaderPanel();
 * panel.getHeader().setCentre("My Title");
 * panel.setBody(myContentComponent);
 * </pre>
 * <p>
 * <strong>Container Types:</strong>
 * </p>
 * <ul>
 * <li>{@link HeaderDialog} - Wraps HeaderPanel in a modal dialog</li>
 * <li>{@link HeaderFrame} - Wraps HeaderPanel in a top-level window</li>
 * <li>{@link HeaderLayer} - Wraps HeaderPanel in an overlay layer</li>
 * </ul>
 *
 * @see HeaderBox
 * @see HeaderDialog
 * @see HeaderFrame
 * @see HeaderLayer
 */
public class HeaderPanel extends LayerPanel {

	private JComponent content;

	private HeaderBox header;
	private Component body;
	private final BodyShadowPanel bodyHost = new BodyShadowPanel();

	public HeaderPanel() {
		this(new HeaderBox(), new JPanel());
	}

	public HeaderPanel(HeaderBox header, Component body) {
		super(false);
		this.header = header;

		content = getContentLayer();
		content.setLayout(new GridBagLayout());
		content.add(header, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Spacing.iNone(), 0, 0));
		content.add(bodyHost, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, Spacing.iNone(), 0, 0));

		setBody(body);
	}
	
	
	public HeaderBox getHeader() {
		return header;
	}


	public Component getBody() {
		return body;
	}
	
	public void setBody(Component body) {
		this.body = body;
		this.bodyHost.setContent(body);

		// Recalculate layout and repaint after component changes
		this.content.revalidate();
		this.content.repaint();
	}

	/**
	 * Sets whether the header casts a soft shadow onto the body. Enabled by default.
	 *
	 * @param headerShadow true to draw the shadow, false to hide it
	 */
	public void setHeaderShadow(boolean headerShadow) {
		this.bodyHost.setShadowEnabled(headerShadow);
	}

	/**
	 * Gets whether the header casts a soft shadow onto the body.
	 *
	 * @return true if the shadow is drawn, false otherwise
	 */
	public boolean isHeaderShadow() {
		return this.bodyHost.isShadowEnabled();
	}

	/**
	 * Fluent variant of {@link #setHeaderShadow(boolean)}.
	 *
	 * @param headerShadow true to draw the shadow, false to hide it
	 * @return this HeaderPanel
	 */
	public HeaderPanel withHeaderShadow(boolean headerShadow) {
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
		this.header.setPaintBackground(paintBackground);
	}

	/**
	 * Gets whether the header paints its background.
	 *
	 * @return true if the background is painted, false otherwise
	 */
	public boolean isHeaderBackgroundPainted() {
		return this.header.isPaintBackground();
	}

	/**
	 * Fluent variant of {@link #setHeaderBackgroundPainted(boolean)}.
	 *
	 * @param paintBackground true to paint the background, false to leave it transparent
	 * @return this HeaderPanel
	 */
	public HeaderPanel withHeaderBackgroundPainted(boolean paintBackground) {
		setHeaderBackgroundPainted(paintBackground);
		return this;
	}

}
