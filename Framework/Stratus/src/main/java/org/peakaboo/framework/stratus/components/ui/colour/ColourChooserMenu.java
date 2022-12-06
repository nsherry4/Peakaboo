package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.util.List;

import javax.swing.JPopupMenu;

public class ColourChooserMenu extends ColourView {

	private List<Color> colours;
	private int columns;
	
	public ColourChooserMenu(List<Color> colours, Color colour) {
		this(colours, colour, 3);
	}
	
	public ColourChooserMenu(List<Color> colours, Color colour, int columns) {
		super(colour);
		this.colours = colours;
		this.columns = columns;
	}

	@Override
	protected void onMouseClick() {
		var popup = new ColourChooserPopup(this, colours, colour, columns);
		int w = (getWidth() - popup.getPreferredSize().width)/2;
		popup.show(this, w, getHeight());
		
	}
	
	void setColour(Color c) {
		this.colour = c;
		repaint();
	}

	static class ColourChooserPopup extends JPopupMenu {

		private ColourChooser chooser;
		
		public ColourChooserPopup(ColourChooserMenu parent, List<Color> colours, Color selected, int columns) {
			chooser = new ColourChooser(colours, selected, columns, false);
			chooser.addItemListener(e -> {
				this.setVisible(false);
				parent.setColour(chooser.getSelected());
			});
			this.add(chooser);
		}
		
	}

	
}
