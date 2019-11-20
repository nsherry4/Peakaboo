package org.peakaboo.framework.swidget.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Builds a basic table model out of a {@link List}
 * @author NAS
 */
public class ListTableModel<T> extends AbstractTableModel {
	
	private List<T> items;
	private int columns;
	
	public ListTableModel(List<T> items) {
		this(items, 1);
	}

	public ListTableModel(List<T> items, int columns) {
		this.items = items;
		this.columns = columns;
	}
	

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return columns;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return items.get(rowIndex);
	}

}
