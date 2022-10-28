package org.peakaboo.framework.swidget.widgets.listwidget;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ListWidgetTableCellRenderer<T> implements TableCellRenderer, ListWidgetParent {

	private ListWidget<T> widget;
	
	public ListWidgetTableCellRenderer(ListWidget<T> widget) {
		this.widget = widget;
		widget.setParent(this);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		if (isSelected) {
			widget.setForeground(table.getSelectionForeground());
			widget.setBackground(table.getSelectionBackground());
			widget.setOpaque(true);
		} else {
			widget.setForeground(table.getForeground());
			widget.setBackground(table.getBackground());
			widget.setOpaque(false);
		}
		
		widget.setValue((T) value, isSelected);
		
		if (table.getRowHeight() < widget.getPreferredSize().height) {
			table.setRowHeight(widget.getPreferredSize().height);
		}
		
		return widget;
		
	}

	@Override
	public void editingStopped() {
		//NOOP
	}

}
