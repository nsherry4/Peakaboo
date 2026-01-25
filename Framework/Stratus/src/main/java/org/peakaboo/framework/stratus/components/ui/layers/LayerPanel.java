package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Optional;
import java.util.Stack;

import javax.swing.*;

import org.peakaboo.framework.stratus.components.ui.live.LiveFrame;


public class LayerPanel extends JLayeredPane {

	private Stack<Layer> layers = new Stack<>();
	private Layer contentLayer;
	private ToastManagerLayer toastManager = null;
	
	
	public LayerPanel(boolean topFade) {
		setLayout(new LayerLayout());	
		contentLayer = new ContentLayer(this, topFade);
		add(contentLayer.getJLayer(), new StackConstraints(JLayeredPane.DEFAULT_LAYER, "content"));
				
	}
	
	

	Layer layerForComponent(Component component) {
		for (Layer layer : layers) {
			if (layer.getContent() == component) {
				return layer;
			}
		}
		return null;
	}
	
	boolean isLayerBlocked(Layer layer) {
		return getBlockingLayer(layer).isPresent();
	}
	
	Optional<Layer> getBlockingLayer(Layer layer) {
		if (layers.isEmpty()) {
			return Optional.empty();
		}
		Layer blocker = null;
		for (int i = layers.size()-1; i >= 0; i--) {
			Layer li = layers.get(i);
			if (layer == li) {
				return Optional.ofNullable(blocker);
			}
			if (li.modal()) {
				blocker = li;
			}
		}
		return Optional.ofNullable(blocker);
	}
	
	
	private static final class StackConstraints {
		public final int layer;
		public final Object layoutConstraints;

		public StackConstraints(int layer, Object layoutConstraints) {
			this.layer = layer;
			this.layoutConstraints = layoutConstraints;
		}
	}

	protected void addImpl(Component comp, Object constraints, int index) {
		int layer = 0;
		int pos = 0;
		Object constr;
		if (constraints instanceof StackConstraints sc) {
			layer = sc.layer;
			constr = sc.layoutConstraints;
		} else {
			layer = getLayer(comp);
			constr = constraints;
		}
		
		pos = insertIndexForLayer(layer, index);
		super.addImpl(comp, constr, pos);
		setLayer(comp, layer, pos);
		comp.validate();
		comp.repaint();
	}

	/**
	 * This exists to work around a bug where nothing seems to be able to trigger a
	 * paint when the window is minimized, to the point where even after
	 * unminimizing it still won't redraw, only redrawing child components on
	 * mouseover. The preferred solution is to replace {@link JFrame}s with
	 * {@link LiveFrame}s, as their sole function is to override this behaviour
	 */
	private void updateIfMinimized() {
		Component croot = SwingUtilities.getRoot(this);
		if (croot instanceof JFrame frame) {
			int state = frame.getState();
			if (state == Frame.ICONIFIED) {
				frame.update(frame.getGraphics());
			}
		}
	}

	private void updateScreen() {
		SwingUtilities.invokeLater(() -> {
			updateIfMinimized();
			this.revalidate();
			this.repaint();
		});
	}
	
	/**
	 * Adds a modal component to the top of the modal stack. This allows more 
	 * than one modal dialog at a time.
	 */
	public void pushLayer(Layer layer) {
		layer.getContent().setFocusCycleRoot(true);
		layers.push(layer);
		
		this.add(layer.getJLayer(), new StackConstraints(layers.size()+200, "modal-layer-" + layers.size()));
		
		layer.getJLayer().requestFocus();
		updateScreen();

		
		
	}

	public void removeLayer(Layer layer) {
		if (!layers.contains(layer)) {
			return;
		}
		
		layers.remove(layer);
		this.remove(layer.getJLayer());
		layer.discard();
		updateScreen();
		

	}

	
	public JComponent getContentLayer() {
		return contentLayer.getContent();
	}
	
	
	/**
	 * Show a toast notification message
	 * @param message the message to display
	 */
	public void showToast(String message) {
		showToast(message, () -> {});
	}

	/**
	 * Show a toast notification message with a click action
	 * @param message the message to display
	 * @param onClick action to perform when the toast is clicked
	 */
	public void showToast(String message, Runnable onClick) {
		if (toastManager == null || !layers.contains(toastManager)) {
			toastManager = new ToastManagerLayer(this);
			pushLayer(toastManager);
		}
		toastManager.addToast(message, onClick);
	}

	public void copyInteraction(String contents, Timer[] debouncerBox) {

		// Copy to clipboard
		StringSelection stringSelection = new StringSelection(contents);
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);

		Timer debouncer = debouncerBox[0];

		// Show confirmation toast with debouncing
		// If timer is already running, suppress this toast
		if (debouncer != null && debouncer.isRunning()) {
			return;
		}

		// Show the toast immediately
		showToast("Copied to clipboard");

		// Start debounce timer to suppress future toasts for 5 seconds
		debouncer = new Timer(5000, e -> {
			// Timer finished, next toast can be shown
		});
		debouncerBox[0] = debouncer;
		debouncer.setRepeats(false);
		debouncer.start();

	}

	/**
	 * Tests if this component either <i>is</i>, or is <i>contained in</i> a LayerPanel.
	 * @return true if this component or one of its transitive parents is a LayerPanel, false otherwise.
	 */
	public static boolean parentOf(Component c) {
		while (c != null) {
			if (c instanceof LayerPanel) {
				return true;
			}
			c = c.getParent();
		}
		return false;
	}
	
	/**
	 * Starting with the given component, tests each parent component until a LayerPanel component is found
	 * @return parent LayerPanel if found, null otherwise
	 */
	public static LayerPanel parentFor(Component c) {
		while (c != null) {
			if (c instanceof LayerPanel lp) {
				return lp;
			}
			c = c.getParent();
		}
		return null;
	}
	
	
}


