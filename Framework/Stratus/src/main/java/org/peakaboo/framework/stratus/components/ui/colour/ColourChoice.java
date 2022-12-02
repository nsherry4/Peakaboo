package org.peakaboo.framework.stratus.components.ui.colour;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.HSLColor;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.theme.BrightTheme;

public class ColourChoice extends ColourComponent implements ItemSelectable {
	
	private boolean selectable, selected;
	private List<ItemListener> listeners = new ArrayList<>();
	
	public ColourChoice(Color colour) {
		this.colour = colour;
		this.selectable = true;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setSelected(!isSelected());
			}
		});
		
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
	
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}



	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (!isSelectable() || this.selected == selected) { return; }
		this.selected = selected;
		repaint();
		updateListeners();
	}



	@Override
	public void paint(Graphics g0) {
		super.paint(g0);
		
		g0 = g0.create();
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		int r = size/2;
		int inset;
		
		g.setColor(this.colour);
		inset = 2;
		g.fill(new RoundRectangle2D.Float(inset, inset, size-inset-2, size-inset-2, r, r));
		
		if (isSelectable() && isSelected()) {
			Path2D shape = new Path2D.Float();
			float pad = inset + 8;
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
