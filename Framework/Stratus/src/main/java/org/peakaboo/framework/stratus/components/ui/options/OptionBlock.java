package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

/**
 * A block of related option entries
 *
 */

public class OptionBlock extends OptionComponent {

	private boolean bordered = true;
	private boolean dividers = true;
	
	public OptionBlock() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	@Override
	public void paintBackground(Graphics2D go) {
		
		Graphics2D g = (Graphics2D) go.create();
		
		g.setColor(bg);
		g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, (int)radius, (int)radius);
		if (bordered) {
			g.setColor(borderAlpha);
			g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, (int)radius, (int)radius);
		}
		
		g.dispose();
		

	}
	
	public List<OptionBox> children() {
		List<OptionBox> children = new ArrayList<>();
		for (Component c : this.getComponents()) {
			if (c instanceof OptionBox o) {
				children.add(o);
			}
		}
		return children;
	}
	
	public OptionBlock withBorder(boolean bordered) {
		this.bordered = bordered;
		repaint();
		return this;
	}
	
	public boolean isBordered() {
		return this.bordered;
	}

	public OptionBlock withDividers(boolean dividers) {
		this.dividers = dividers;
		repaint();
		return this;
	}
	
	public boolean hasDividers() {
		// TODO Auto-generated method stub
		return dividers;
	}
	
}
