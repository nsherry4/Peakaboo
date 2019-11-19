package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.peakaboo.framework.swidget.live.LiveFrame;


public class LayerPanel extends JLayeredPane {

	private Stack<Layer> layers = new Stack<>();
	private Layer contentLayer;
	
	
	
	
	public LayerPanel() {
		setLayout(new LayerLayout());	
		contentLayer = new ContentLayer(this);
		add(contentLayer.getJLayer(), new StackConstraints(JLayeredPane.DEFAULT_LAYER, "content"));
				
	}
	
	

	Layer layerForComponent(Component component) {
		for (Layer layer : layers) {
			if (layer.getComponent() == component) {
				return layer;
			}
		}
		return null;
	}
	
	boolean isLayerBlocked(Layer layer) {
		boolean blocked = false;
		if (layers.isEmpty()) {
			return blocked;
		}
		for (int i = layers.size()-1; i >= 0; i--) {
			Layer li = layers.get(i);
			if (layer == li) {
				return blocked;
			}
			if (li.modal()) {
				blocked = true;
			}
		}
		return blocked;
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
		if (constraints instanceof StackConstraints) {
			layer = ((StackConstraints) constraints).layer;
			constr = ((StackConstraints) constraints).layoutConstraints;
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
		if (croot instanceof JFrame) {
			JFrame frame = ((JFrame)croot);
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
		layer.getComponent().setFocusCycleRoot(true);
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
		return contentLayer.getComponent();
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
			if (c instanceof LayerPanel) {
				return (LayerPanel) c;
			}
			c = c.getParent();
		}
		return null;
	}
	

}


