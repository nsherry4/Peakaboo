package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;

public abstract class ColourView extends ColourComponent {

	public static final int DEFAULT_PAD = 2;

	public static record Settings (int size, float stroke, int pad) {};
	protected Settings settings;
	
	public ColourView(Color colour) {
		this(colour, new Settings(DEFAULT_SIZE, 0f, DEFAULT_PAD));
	}
	
	public ColourView(Color colour, Settings settings) {
		super(settings.size());
		this.colour = colour;
		this.settings = settings;
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent arg0) {
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
		int pad = settings.pad();
		
		g.setColor(this.colour);
		g.fillRoundRect(pad, pad, size-pad-2, size-pad-2, r, r);

		if (this.settings.stroke() > 0) {
			g.setColor(StratusColour.blackOrWhite(this.colour));
			g.setStroke(new BasicStroke(this.settings.stroke()));
			g.drawRoundRect(pad, pad, size-pad-2, size-pad-2, r, r);
		}
		
		g.dispose();
		
	}
	
}
