package org.peakaboo.framework.stratus.components.stencil;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.peakaboo.framework.stratus.api.StratusColour;

public class StencilListCellRenderer<T> implements ListCellRenderer<T>, StencilParent {

	private Stencil<T> widget;
	
	public StencilListCellRenderer(Stencil<T> widget) {
		this.widget = widget;
		widget.setParent(this);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		if (isSelected) {
			widget.setForeground(StratusColour.explicit(list.getSelectionForeground()));
			widget.setBackground(list.getSelectionBackground());
			widget.setOpaque(true);
		} else {
			widget.setForeground(StratusColour.explicit(list.getForeground()));
			widget.setBackground(list.getBackground());
			widget.setOpaque(false);
		}
		
		widget.setValue((T) value, isSelected);
			
		return widget;
		
	}

	@Override
	public void editingStopped() {
		//NOOP
	}



}
