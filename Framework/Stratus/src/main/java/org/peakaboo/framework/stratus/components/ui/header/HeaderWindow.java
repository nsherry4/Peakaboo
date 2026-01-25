package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.Component;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

/**
 * Common interface for windows that contain a HeaderPanel.
 * <p>
 * This interface provides default implementations for common operations on header-based
 * windows, eliminating code duplication between {@link HeaderDialog} and {@link HeaderFrame}.
 * Both classes extend different Swing window types but share identical header management logic,
 * which is provided by this interface's default methods.
 * </p>
 * <p>
 * <strong>Implementations:</strong>
 * </p>
 * <ul>
 * <li>{@link HeaderDialog} - Modal dialog with HeaderPanel</li>
 * <li>{@link HeaderFrame} - Top-level window with HeaderPanel</li>
 * </ul>
 * <p>
 * Implementing classes must provide {@link #getRootPanel()}, {@link #packWindow()}, and {@link #close()}.
 * All other methods have default implementations that delegate to the root panel.
 * </p>
 *
 * @see HeaderPanel
 * @see HeaderBox
 */
interface HeaderWindow {

	/**
	 * Gets the root HeaderPanel for this window.
	 *
	 * @return the HeaderPanel containing the header and body
	 */
	HeaderPanel getRootPanel();

	/**
	 * Packs this window to fit its contents.
	 * This method must be implemented by the concrete window class.
	 */
	void packWindow();

	/**
	 * Closes this window, hiding it and cleaning up resources.
	 */
	void close();

	/**
	 * Gets the header component.
	 *
	 * @return the HeaderBox
	 */
	default HeaderBox getHeader() {
		return getRootPanel().getHeader();
	}

	/**
	 * Gets the body component.
	 *
	 * @return the body component, or null if not set
	 */
	default Component getBody() {
		return getRootPanel().getBody();
	}

	/**
	 * Sets the body component and repacks the window.
	 *
	 * @param body the component to use as the body
	 */
	default void setBody(Component body) {
		getRootPanel().setBody(body);
		packWindow();
	}

	/**
	 * Gets the LayerPanel that serves as the content root.
	 *
	 * @return the root LayerPanel
	 */
	default LayerPanel getLayerRoot() {
		return getRootPanel();
	}

	/**
	 * Creates a HeaderPanel configured for use in header windows.
	 * The panel is created with a themed border matching the Stratus look and feel.
	 *
	 * @return a configured HeaderPanel
	 */
	default HeaderPanel createHeaderPanel() {
		HeaderPanel panel = new HeaderPanel();
		panel.setBorder(new javax.swing.border.MatteBorder(1, 1, 1, 1,
			Stratus.getTheme().getWidgetBorder()));
		panel.getHeader().setShowClose(true);
		panel.getHeader().setOnClose(this::close);
		return panel;
	}

}