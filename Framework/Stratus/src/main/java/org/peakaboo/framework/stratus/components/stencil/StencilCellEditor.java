package org.peakaboo.framework.stratus.components.stencil;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import org.peakaboo.framework.stratus.api.StratusColour;

public class StencilCellEditor<T> extends DefaultCellEditor implements StencilParent {

	private Stencil<T> widget;
	
	public StencilCellEditor(Stencil<T> widget) {
		super(new JCheckBox());
		this.widget = widget;
		widget.setParent(this);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		widget.setForeground(StratusColour.explicit(table.getSelectionForeground()));
		widget.setBackground(table.getSelectionBackground());
		widget.setOpaque(true);
				
		widget.setValue((T) value, isSelected);
		
		return widget;
	}
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public void editingStopped() {
		super.fireEditingStopped();
	}

}
