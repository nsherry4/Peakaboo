package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

import org.jdesktop.swingx.image.FastBlurFilter;
import org.peakaboo.framework.swidget.graphics.ManagedImageBuffer;

class LayerBlurUI<T extends Component> extends LayerUI<T> {
	private BufferedImageOp mOperation;
	private ManagedImageBuffer bufferer;

	private LayerPanel parent;
	private Component component;
	
	public LayerBlurUI(LayerPanel parent, Component component) {
		this.parent = parent;
		this.component = component;
		mOperation = new FastBlurFilter(1);
		bufferer = new ManagedImageBuffer();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Layer layer = parent.layerForComponent(this.component);
			
		if (!parent.isLayerBlocked(layer)) {
			super.paint(g, c);
			
		} else {
			int w = c.getWidth();
			int h = c.getHeight();
	
			if (w == 0 || h == 0) {
				return;
			}
	
			bufferer.resize(w, h);
			bufferer.clear();
			BufferedImage buffer = bufferer.get();
			
			Graphics2D ig2 = buffer.createGraphics();
			super.paint(ig2, c);
			ig2.dispose();
	
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setClip(g.getClip());
			if (LayerPanelConfig.blur) {
				g2.drawImage(buffer, mOperation, 0, 0);
			} else {
				g2.drawImage(buffer, null, 0, 0);
			}
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, c.getWidth(), c.getHeight());
			g2.dispose();
			
		}
	}
}
