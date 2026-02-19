package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.border.MatteBorder;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.live.LiveFrame;

/**
 * An undecorated top-level window with a {@link HeaderPanel}.
 * <p>
 * HeaderFrame provides a styled window with a custom header bar and resizable edges.
 * The frame is undecorated (no native window controls) and uses a {@link HeaderBox} for the
 * title bar, which includes an optional close button and supports window dragging.
 * </p>
 * <p>
 * <strong>Features:</strong>
 * </p>
 * <ul>
 * <li>Custom header bar with configurable left, centre, and right sections</li>
 * <li>Built-in close button (shown by default)</li>
 * <li>Window dragging via header bar</li>
 * <li>Edge-based window resizing via glass pane</li>
 * <li>Themed border matching the Stratus look and feel</li>
 * </ul>
 * <p>
 * <strong>When to Use:</strong>
 * </p>
 * <ul>
 * <li>Use HeaderFrame for non-modal top-level windows</li>
 * <li>Use {@link HeaderDialog} for modal dialogs</li>
 * <li>Use {@link HeaderLayer} for in-window overlays (not separate windows)</li>
 * </ul>
 *
 * @see HeaderDialog
 * @see HeaderPanel
 * @see HeaderBox
 * @see HeaderLayer
 */
public class HeaderFrame extends LiveFrame implements HeaderWindow {

	private HeaderPanel root;
	private Runnable onClose;
	private HeaderFrameGlassPane glass;
	
	public HeaderFrame() {
		this(() -> {});
	}
	
	public HeaderFrame(Runnable onClose) {
		this.onClose = onClose;

		this.setUndecorated(true);

		root = createHeaderPanel();
		this.setContentPane(root);

		glass = new HeaderFrameGlassPane(this);
		this.setGlassPane(glass);
		glass.setVisible(true);
				
		Toolkit.getDefaultToolkit().addAWTEventListener(glass, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);

	}

	@Override
	public void close() {
		this.setVisible(false);
		Toolkit.getDefaultToolkit().removeAWTEventListener(glass);
		this.onClose.run();
	}

	@Override
	public HeaderPanel getRootPanel() {
		return root;
	}

	@Override
	public void packWindow() {
		pack();
	}
	
	@Override
	public void pack() {
		// Ensure any pending header rebuilds happen before packing
		root.getHeader().ensureBuilt();
		super.pack();
	}

}
