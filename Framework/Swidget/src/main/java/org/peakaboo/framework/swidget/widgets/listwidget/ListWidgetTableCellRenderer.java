package org.peakaboo.framework.swidget.widgets.listwidget;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ListWidgetTableCellRenderer<T> implements TableCellRenderer, ListWidgetParent {

	private ListWidget<T> widget;
	
	public ListWidgetTableCellRenderer(ListWidget<T> widget) {
		this.widget = widget;
		widget.setParent(this);
	}
	
	/**
	 * Constructor that pre-adjusts the table's row-height to not be less than the
	 * widget's preferred height. Otherwise, this will be done at render-time and
	 * can result it a glitchy first-rendering
	 */
	public ListWidgetTableCellRenderer(ListWidget<T> widget, JTable table) {
		this(widget);
		checkHeight(table);
	}
	
	private void checkHeight(JTable table) {
		if (table.getRowHeight() < widget.getPreferredSize().height) {
			table.setRowHeight(widget.getPreferredSize().height);
		}
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
		
		checkHeight(table);
		
		return widget;
		
	}

	@Override
	public void editingStopped() {
		//NOOP
	}

}
