package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.stratus.api.HSLColor;
import org.peakaboo.framework.stratus.api.Stratus;

public class ColourChoice extends ColourView implements ItemSelectable {
	
	private boolean selected, deselectable;
	private List<ItemListener> listeners = new ArrayList<>();
	
	public ColourChoice(Color colour) {
		super(colour);
		this.deselectable = true;
	}
	
	@Override
	protected void onMouseClick() {
		if (isDeselectable()) {
			setSelected(!isSelected());
		} else {
			setSelected(true);
		}
	}
	
	protected void updateListeners() {
		for (var listener : listeners) {
			listener.itemStateChanged(new ItemEvent(
					ColourChoice.this, 
					ItemEvent.ITEM_STATE_CHANGED, 
					ColourChoice.this,
					isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED
				));
		}
	}

	/**
	 * Indicates if this component will become deselected if it is clicked on while
	 * already selected.
	 */
	public boolean isDeselectable() {
		return deselectable;
	}

	public void setDeselectable(boolean deselectable) {
		this.deselectable = deselectable;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		//If an item is not deselectable, we want to emit re-selection events
		if (isDeselectable() && this.selected == selected) { return; }
		this.selected = selected;
		repaint();
		updateListeners();
	}
	



	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		
		Graphics2D g = Stratus.g2d(g0);
		
		if (isSelected()) {
			Path2D shape = new Path2D.Float();
			float pad = INSET + 8;
			float voffset = 0;
			float inner = size - (pad*2);
			float halfin= inner/2;
			
			shape.moveTo(pad, pad+halfin+voffset);
			shape.lineTo(pad+(inner/3), pad+inner+voffset);
			shape.lineTo(pad+inner, pad+voffset);

			float lum = new HSLColor(this.colour).getLuminance();
			Color highlight;
			if (lum < 60) {
				highlight = Stratus.getTheme().getPalette().getColour("Light", "1");
			} else {
				highlight = Stratus.getTheme().getPalette().getColour("Dark", "5");
			}
			g.setColor(highlight);
			
			g.setStroke(new BasicStroke(2.5f));
			g.draw(shape);


		}
		
		g.dispose();
		
	}

	@Override
	public void addItemListener(ItemListener listener) {
		listeners.add(listener);
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { this };
	}

	@Override
	public void removeItemListener(ItemListener listener) {
		listeners.remove(listener);
	}
	
}
