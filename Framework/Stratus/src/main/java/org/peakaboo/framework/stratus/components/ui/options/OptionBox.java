package org.peakaboo.framework.stratus.components.ui.options;

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

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;

/**
 * An individual option entry
 *
 */
public class OptionBox extends OptionComponent {

	private OptionBlock block = null;
	private boolean hover = false;
	
	public OptionBox() {
		this(null);
	}
	
	public OptionBox(OptionBlock block) {
		this.block = block;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPadding(padding);
		
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
	
	void setPadding(int size) {
		this.padding = size;
		this.setBorder(new EmptyBorder(padding, (int)(padding*1.3f), padding, (int)(padding*1.3f)));
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
	
		Color highlight = new Color(0x0A000000, true);
		
		if (block == null) {
			g.setColor(bg);
			g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 0, 0);
			if (hover && isEnabled()) {
				g.setColor(highlight);
				g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 0, 0);
			}
			g.setColor(borderAlpha);
			g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 0, 0);
			
		} else {
			int index = block.children().indexOf(this);
			int count = block.children().size();
			if (index != count-1 && block.hasDividers()) {
				g.setColor(StratusColour.lighten(borderAlpha, 0.5f));
				g.drawLine(1, getHeight()-1, getWidth()-1, getHeight()-1);
			}
			if (hover && isEnabled()) {
				
				Area area = new Area();
				
				if (block.isBordered()) {
					Shape outline;
					if (index > 0 && index < count - 1) {
						//somewhere in the middle
						outline = new Rectangle(0, 0, getWidth()-1, getHeight()-1);
						area.add(new Area(outline));
					} else {
						outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
						area.add(new Area(outline));
						if (index > 0) {
							area.add(new Area(new Rectangle(0, 0, getWidth()-1, (int)radius+1)));
						}
						if (index < count-1) {
							area.add(new Area(new Rectangle(0, (int)(getHeight()-radius-1), getWidth()-1, getHeight()-1)));
						}
					}

				} else {
					Shape outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
					area.add(new Area(outline));
				}
				
				g.setColor(highlight);
				g.fill(area);
			}
			
			
		}
		
	}
	
	public void setBlock(OptionBlock block) {
		this.block = block;
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
