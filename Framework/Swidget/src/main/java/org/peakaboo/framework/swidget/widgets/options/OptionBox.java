package org.peakaboo.framework.swidget.widgets.options;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.swidget.widgets.Spacing;

/**
 * An individual option entry
 *
 */
public class OptionBox extends OptionComponent {

	private OptionBlock block = null;
	
	public OptionBox() {
		this(null);
	}
	
	public OptionBox(OptionBlock block) {
		this.block = block;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new EmptyBorder(padding, padding, padding, padding));
	}
	
	public void addSpacer() {
		this.add(Box.createHorizontalStrut(Spacing.medium));
		this.add(Box.createHorizontalGlue());
	}
	
	@Override
	public void paintBackground(Graphics2D g) {
	
		if (block == null) {
			Shape outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 0, 0);
			g.setColor(bg);
			g.fill(outline);
			g.setColor(borderAlpha);
			g.draw(outline);
		} else {
			if (block.children().indexOf(this) != block.children().size()-1) {
				g.setColor(Stratus.lighten(borderAlpha, 0.5f));
				g.drawLine(1, getHeight()-1, getWidth()-1, getHeight()-1);
			}
		}

	}
	
	@Override
	public Dimension getPreferredSize() {
		
		int x = 0, y = 0;
		for (Component c : this.getComponents()) {
			Dimension d = c.getPreferredSize();
			x += d.width;
			y = Math.max(y, d.height);
		}
				
		return new Dimension(x + padding*2, y + padding*2);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	

	
}
