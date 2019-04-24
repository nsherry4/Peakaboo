package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

import org.jdesktop.swingx.image.FastBlurFilter;

public class LayerBlurUI<T extends Component> extends LayerUI<T> {
	private BufferedImage mOffscreenImage;
	private BufferedImageOp mOperation;

	private LayerPanel parent;
	private Component component;
	
	public LayerBlurUI(LayerPanel parent, Component component) {
		this.parent = parent;
		this.component = component;
		mOperation = new FastBlurFilter(1);
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
	
			// Only create the offscreen image if the one we have
			// is the wrong size.
			if (mOffscreenImage == null || mOffscreenImage.getWidth() != w || mOffscreenImage.getHeight() != h) {
				mOffscreenImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			}

			Graphics2D ig2 = mOffscreenImage.createGraphics();
			ig2.setBackground(new Color(0, 0, 0, 0));
			ig2.clearRect(0, 0, c.getWidth(), c.getHeight());
			ig2.dispose();
			
			ig2 = mOffscreenImage.createGraphics();
			super.paint(ig2, c);
			ig2.dispose();
	
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setClip(g.getClip());
			if (LayerPanelConfig.blur) {
				g2.drawImage(mOffscreenImage, mOperation, 0, 0);
			} else {
				g2.drawImage(mOffscreenImage, null, 0, 0);
			}
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, c.getWidth(), c.getHeight());
			g2.dispose();
			
		}
	}
}
