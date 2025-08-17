package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;

class LayerShadeUI<T extends Component> extends LayerUI<T> {
	
	private LayerPanel parent;
	private Component component;
	private boolean topFade;
	
	public LayerShadeUI(LayerPanel parent, Component component) {
		this.parent = parent;
		this.component = component;
		this.topFade = false;
	}

	public void setTopFade(boolean topFade) {
		this.topFade = topFade;
	}
	
	public boolean isTopFade() {
		return topFade;
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		Layer layer = parent.layerForComponent(this.component);
		
		if (!parent.isLayerBlocked(layer)) {
			super.paint(g, c);
			
		} else {
			Graphics2D g2 = (Graphics2D) g.create();
			
			// Paint the component normally first
			super.paint(g2, c);
			
			// Darken the background
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			g2.setColor(Color.BLACK);
			final int GRADIENT_HEIGHT = 3;
			
			// Add shadow and border effects
			Optional<Layer> optAboveLayer = parent.getBlockingLayer(layer);
			if (optAboveLayer.isPresent()) {
				Layer aboveLayer = optAboveLayer.get();
				var above = aboveLayer.getOuterComponent();
				if (above != null) {
					if (Stratus.lowGraphicsMode) {
						// Draw 1px border around upper layer in low graphics mode
						g2.setColor(StratusColour.lessTransparent(Stratus.getTheme().getWidgetBorderAlpha(), 0.2f));
						g2.drawRect(above.getX() - 1, above.getY() - 1, above.getWidth() + 1, above.getHeight() + 1);
					} else {
						// Draw multi-layer drop shadow with matching corner radius
						float cornerRadius = aboveLayer.getCornerRadius();
						int shadowLayers = 8;
						for (int i = shadowLayers; i > 0; i--) {
							// Accommodate rounded corners by continuing the gradient underneath the above layer
							float offset = i - 2;
							// Non-linear falloff
							float alpha = 0.4f / i;
							var shadow = new RoundRectangle2D.Float(
								above.getX() - offset,
								above.getY() - offset + 2,
								above.getWidth() + offset * 2,
								above.getHeight() + offset * 2,
								cornerRadius + offset,
								cornerRadius + offset
							);
							g2.setColor(new Color(0, 0, 0, alpha));
							g2.fill(shadow);
						}
					}
				}
			}



			if (!topFade) {
				g2.fillRect(0, 0, c.getWidth(), c.getHeight());
			} else {
				// If we are fading from the top pixels, draw a gradient
				g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, GRADIENT_HEIGHT, new Color(0, 0, 0, 255)));
				g2.fillRect(0, 0, c.getWidth(), GRADIENT_HEIGHT);
				g2.fillRect(0, GRADIENT_HEIGHT, c.getWidth(), c.getHeight());
			}
			
			g2.dispose();
		}
	}
	
	
}
