package org.peakaboo.framework.swidget.widgets.options;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
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
public abstract class OptionBox extends OptionComponent {

	private OptionBlock block = null;
	private boolean hover = false;
	
	public OptionBox() {
		this(null);
	}
	
	public OptionBox(OptionBlock block) {
		this.block = block;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new EmptyBorder(padding, padding, padding, padding));
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent arg0) {
				hover = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				hover = false;
				repaint();
			}
			
		});
		
	}
	
	public void addExpander() {
		this.add(Box.createHorizontalStrut(Spacing.medium));
		this.add(Box.createHorizontalGlue());
	}
	
	public void addSpacer() {
		this.add(Box.createHorizontalStrut(Spacing.large));
	}
	
	@Override
	public void paintBackground(Graphics2D g) {
	
		if (block == null) {
			Shape outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 0, 0);
			g.setColor(bg);
			g.fill(outline);
			if (hover) {
				g.setColor(new Color(0x20000000, true));
				g.fill(outline);
			}
			g.setColor(borderAlpha);
			g.draw(outline);
			
		} else {
			int index = block.children().indexOf(this);
			int count = block.children().size();
			if (index != count-1 && block.hasDividers()) {
				g.setColor(Stratus.lighten(borderAlpha, 0.5f));
				g.drawLine(1, getHeight()-1, getWidth()-1, getHeight()-1);
			}
			if (hover) {
				Area area = new Area();
				Shape outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
				area.add(new Area(outline));
				if (index > 0) {
					area.add(new Area(new Rectangle(0, 0, getWidth()-1, (int)radius+1)));
				}
				if (index < count-1) {
					area.add(new Area(new Rectangle(0, (int)(getHeight()-radius-1), getWidth()-1, getHeight()-1)));
				}
				
				g.setColor(new Color(0x08000000, true));
				g.fill(area);
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

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
	}

}
