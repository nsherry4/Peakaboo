package org.peakaboo.framework.swidget.widgets.settings;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.peakaboo.framework.swidget.widgets.ClearPanel;

public abstract class PaintedComponent extends ClearPanel {

	@Override
	public void paintBorder(Graphics g) {
		
		g = g.create();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		paintBackground(g2);
		g2.dispose();
		
		super.paintBorder(g);
		
	}
	
	protected abstract void paintBackground(Graphics2D g);
	
	
}
