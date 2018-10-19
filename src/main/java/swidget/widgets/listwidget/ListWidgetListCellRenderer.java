package swidget.widgets.listwidget;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListWidgetListCellRenderer<T> implements ListCellRenderer<T>{

	private ListWidget<T> widget;
	
	public ListWidgetListCellRenderer(ListWidget<T> widget) {
		this.widget = widget;
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		if (isSelected) {
			widget.setForeground(list.getSelectionForeground());
			widget.setBackground(list.getSelectionBackground());
			widget.setOpaque(true);
		} else {
			widget.setForeground(list.getForeground());
			widget.setBackground(list.getBackground());
			widget.setOpaque(false);
		}
		
		widget.setValue((T) value);
			
		return widget;
		
	}



}
