package swidget.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ListTableModel<T> extends AbstractTableModel {
	
	private List<T> items;
	
	public ListTableModel(List<T> items) {
		this.items = items;
	}

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return items.get(rowIndex);
	}

}
