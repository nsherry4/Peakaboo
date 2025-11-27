package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.Spacing;
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
		
	public HeaderPanel() {
		this(new HeaderBox(), new JPanel());
	}
	
	public HeaderPanel(HeaderBox header, Component body) {
		super(false);
		this.header = header;
		
		content = getContentLayer();
		content.setLayout(new GridBagLayout());
		content.add(header, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Spacing.iNone(), 0, 0));
		
		setBody(body);
	}
	
	
	public HeaderBox getHeader() {
		return header;
	}


	public Component getBody() {
		return body;
	}
	
	public void setBody(Component body) {
		if (this.body != null) {
			this.content.remove(this.body);
		}
		
		this.body = body;
		
		if (this.body != null) {
			this.content.add(this.body, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, Spacing.iNone(), 0, 0));
		}
	}

	
}
