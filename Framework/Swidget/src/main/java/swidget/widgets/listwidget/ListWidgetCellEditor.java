package swidget.widgets.listwidget;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class ListWidgetCellEditor<T> extends DefaultCellEditor implements ListWidgetParent {

	private ListWidget<T> widget;
	
	public ListWidgetCellEditor(ListWidget<T> widget) {
		super(new JCheckBox());
		this.widget = widget;
		widget.setParent(this);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		widget.setForeground(table.getSelectionForeground());
		widget.setBackground(table.getSelectionBackground());
		widget.setOpaque(true);
				
		widget.setValue((T) value);
		
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
