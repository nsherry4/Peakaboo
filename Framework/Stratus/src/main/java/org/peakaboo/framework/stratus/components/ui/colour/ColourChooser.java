package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class ColourChooser extends ClearPanel implements ItemSelectable {

	private Color selected;
	private List<Color> options;
	private List<ColourChoice> circles;
	private boolean allowNull = true;
	
	private List<ItemListener> listeners = new ArrayList<>();
	
	public ColourChooser(List<Color> colours, Color selected) {
		this(colours, selected, 1000);
	}
	
	public ColourChooser(List<Color> colours, Color selected, int columns) {
		this(colours, selected, columns, true);
	}
	
	public ColourChooser(List<Color> colours, Color selected, boolean allowNull) {
		this(colours, selected, 1000, allowNull);
	}
	
	public ColourChooser(List<Color> colours, Color selected, int columns, boolean allowNull) {
		this.allowNull = allowNull;
		this.options = colours;
		circles = new ArrayList<ColourChoice>();

		setLayout(new GridLayout((int)Math.ceil((float)colours.size() / (float)columns), columns, Spacing.small, Spacing.small));
		this.setBorder(Spacing.bSmall());
		
		for (var c : colours) {
			ColourChoice widget = new ColourChoice(c);
			widget.setDeselectable(allowNull);
			if (c.equals(selected)) {
				widget.setSelected(true);
			}
			circles.add(widget);
			widget.addItemListener(e -> {
				
				if (widget.isSelected()) {
					
					//Update the selection model based on this selection
					setSelected(widget.getColour());
					
				} else {
					
					//If this widget was deselected, check if our last selected colour matches.
					//If this colour was last selected, clear it now
					if (widget.getColour().equals(this.selected)) {
						setSelected(null);
					}
					
				}

			});
			this.add(widget);
		}
		
		setSelected(selected);
		
	}

	public void setSelected(Color c) {
		if (c == null && !allowNull) {
			throw new IllegalArgumentException("Colour must not be null");
		}
		if (!options.contains(c) && c != null) {
			throw new IllegalArgumentException("Colour is not on the list");
		}
		if (this.selected == c) {
			if (!allowNull) updateListeners();
			return; //nothing else to do
		} else {
			this.selected = c;
			for (var o : circles) {
				o.setSelected(o.getColour().equals(this.selected));
			}
			updateListeners();
		}
	}
	
	public Color getSelected() {
		return selected;
	}

	protected void updateListeners() {
		for (var listener : listeners) {
			listener.itemStateChanged(new ItemEvent(
					ColourChooser.this, 
					ItemEvent.ITEM_STATE_CHANGED, 
					selected,
					ItemEvent.SELECTED
				));
		}
	}
	
	
	@Override
	public void addItemListener(ItemListener listener) {
		listeners.add(listener);
	}

	@Override
	public Object[] getSelectedObjects() {
		return circles.stream()
				.filter(ColourChoice::isSelected)
				.toArray();
	}

	@Override
	public void removeItemListener(ItemListener listener) {
		listeners.remove(listener);		
	}

	
}
