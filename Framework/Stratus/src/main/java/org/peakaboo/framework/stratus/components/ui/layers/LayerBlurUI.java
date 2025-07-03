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

import org.jdesktop.swingx.image.AbstractFilter;
import org.jdesktop.swingx.image.StackBlurFilter;
import org.peakaboo.framework.stratus.api.ManagedImageBuffer;
import org.peakaboo.framework.stratus.api.Stratus;

class LayerBlurUI<T extends Component> extends LayerUI<T> {
	private AbstractFilter mOperation;
	
	private ManagedImageBuffer paintBufferer, blurBufferer;

	private LayerPanel parent;
	private Component component;
	private long lastTime;
	private Optional<Layer> lastBlocker;
	private boolean topFade;
	
	public LayerBlurUI(LayerPanel parent, Component component) {
		this.parent = parent;
		this.component = component;
		mOperation = new StackBlurFilter(2, 2);
		paintBufferer = new ManagedImageBuffer();
		blurBufferer = new ManagedImageBuffer();
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

			Optional<Layer> blocker = parent.getBlockingLayer(layer);
			
			int w = c.getWidth();
			int h = c.getHeight();
	
			if (w == 0 || h == 0) {
				return;
			}
	
			paintBufferer.resize(w, h);
			blurBufferer.resize(w, h);
			var blurBuffer = blurBufferer.get();
	
			
			
			//Calculate if we should repaint or just redraw from buffer
			boolean doRepaint = false;	
			long time = System.currentTimeMillis();
			int repaintInterval = 500;
			doRepaint |= (time - lastTime > repaintInterval);
			doRepaint |= !blurBufferer.isPainted();
			doRepaint |= !blocker.equals(lastBlocker);

			
			if (doRepaint) {
				paintBufferer.clear();
				var paintBuffer = paintBufferer.get();
				
				//Paint the window contents to the first buffer
				Graphics2D pg = paintBuffer.createGraphics();
				super.paint(pg, c);
				
				//Paint the shadow of the above layer
				Optional<Layer> optAboveLayer = parent.getBlockingLayer(layer);
				if (optAboveLayer.isPresent()) {
					Layer aboveLayer = parent.getBlockingLayer(layer).get();
					var above = aboveLayer.getOuterComponent();
					if (above != null) {
						var shadow = new RoundRectangle2D.Float(above.getX(), above.getY() + 1, above.getWidth(), above.getHeight() + 0.5f, aboveLayer.getCornerRadius(), aboveLayer.getCornerRadius());
						pg.setColor(new Color(0x66000000, true));
						pg.fill(shadow);
					}
				}
				
				pg.dispose();
				paintBufferer.markPainted();
				
				//Paint the first buffer to the second buffer with the blur filter
				blurBufferer.clear();
				blurBuffer = blurBufferer.get();
				Graphics2D bg = blurBuffer.createGraphics();
				
				//First draw the painter buffer to the blur buffer, blurring (usually)
				if (Stratus.lowGraphicsMode) {
					bg.drawImage(paintBuffer, null, 0, 0);
				} else {
					bg.drawImage(paintBuffer, mOperation, 0, 0);
				}
				
				//Then darken the background
				bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
				bg.setColor(Color.BLACK);
				final int GRADIENT_HEIGHT = 3;
				if (!topFade) {
					bg.fillRect(0, 0, c.getWidth(), c.getHeight());
				} else {
					// If we are fading from the top 16 pixels, we need to draw a gradient
					// with a GradientPaint
					bg.setPaint(new java.awt.GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, GRADIENT_HEIGHT, new Color(0, 0, 0, 255)));
					bg.fillRect(0, 0, c.getWidth(), GRADIENT_HEIGHT);
					bg.fillRect(0, GRADIENT_HEIGHT, c.getWidth(), c.getHeight());
				}
				
				//Mark this buffer as freshly painted
				bg.dispose();
				blurBufferer.markPainted();
				
				//Reset the time of the last buffer update
				lastTime = time;
				lastBlocker = blocker;
			}
			
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setClip(g.getClip());
			g2.drawImage(blurBuffer, null, 0, 0);
			
			g2.dispose();
			
			
		}
	}
	
	
}
