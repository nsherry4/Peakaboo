package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.border.MatteBorder;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.live.LiveDialog;

/**
 * An undecorated modal dialog with a {@link HeaderPanel}.
 * <p>
 * HeaderDialog provides a styled dialog window with a custom header bar and resizable edges.
 * The dialog is undecorated (no native window controls) and uses a {@link HeaderBox} for the
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
 * <li>Use HeaderDialog for modal dialogs that need custom headers</li>
 * <li>Use {@link HeaderFrame} for non-modal top-level windows</li>
 * <li>Use {@link HeaderLayer} for in-window overlays (not separate windows)</li>
 * </ul>
 *
 * @see HeaderFrame
 * @see HeaderPanel
 * @see HeaderBox
 * @see HeaderLayer
 */
public class HeaderDialog extends LiveDialog implements HeaderWindow {


	private HeaderPanel root;
	private Runnable onClose;
	private HeaderFrameGlassPane glass;
	
	public HeaderDialog(Window parent) {
		this(parent, () -> {});
	}
	
	public HeaderDialog(Window parent, Runnable onClose) {
		super(parent);
		this.onClose = onClose;

		this.setUndecorated(true);

		root = createHeaderPanel();
		this.setContentPane(root);

		glass = new HeaderFrameGlassPane(this);
		this.setGlassPane(glass);
		glass.setVisible(true);
				
		Toolkit.getDefaultToolkit().addAWTEventListener(glass, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		
	}

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
