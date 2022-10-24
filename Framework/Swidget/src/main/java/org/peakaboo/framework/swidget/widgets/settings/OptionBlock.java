package org.peakaboo.framework.swidget.widgets.settings;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

public class OptionBlock extends OptionComponent {

	public OptionBlock() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	@Override
	public void paintBackground(Graphics2D g) {
		
		Shape outline = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
		g.setColor(bg);
		g.fill(outline);
		g.setColor(border);
		g.draw(outline);

	}
	
	public List<OptionBox> children() {
		List<OptionBox> children = new ArrayList<>();
		for (Component c : this.getComponents()) {
			if (c instanceof OptionBox) {
				children.add((OptionBox) c);
			}
		}
		return children;
	}

}
