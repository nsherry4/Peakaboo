package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ColourChooser extends JPanel {

	private Color selected;
	private List<Color> options;
	private List<ColourChoice> circles;
	
	public ColourChooser(List<Color> colours, Color selected) {
		
		this.options = colours;
		this.selected = selected;
		circles = new ArrayList<ColourChoice>();
		
		for (var c : colours) {
			ColourChoice widget = new ColourChoice(c);
			if (c.equals(selected)) {
				widget.setSelected(true);
			}
			circles.add(widget);
			widget.addItemListener(e -> {
				//clear the selected colour, we will rediscover it
				this.selected = null;
				for (var o : circles) {
					//if this circle is selected, set it as the selected colour
					if (o.isSelected()) {
						this.selected = o.getColour();
					}
					//Skip this next part for ourselves -- if we're selected, turn the others off
					if (o == widget) continue;
					if (widget.isSelected()) {
						o.setSelected(false);
					}
				}
			});
			this.add(widget);
		}
		
	}
	
	public Color getColour() {
		return selected;
	}

	
}
