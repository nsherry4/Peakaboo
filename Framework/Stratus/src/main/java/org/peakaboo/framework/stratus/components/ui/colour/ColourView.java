package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import org.peakaboo.framework.stratus.api.Stratus;

public abstract class ColourView extends ColourComponent {

	protected static final int INSET = 2;

	public ColourView(Color colour) {
		this.colour = colour;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				onMouseClick();
			}
		});
	}
	
	protected abstract void onMouseClick();
	
	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		
		Graphics2D g = Stratus.g2d(g0);

		int r = size/2;
		
		g.setColor(this.colour);
		g.fill(new RoundRectangle2D.Float(INSET, INSET, size-INSET-2, size-INSET-2, r, r));

		
		g.dispose();
		
	}
	
}
