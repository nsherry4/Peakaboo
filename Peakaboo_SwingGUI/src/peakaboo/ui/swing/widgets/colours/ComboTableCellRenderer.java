package peakaboo.ui.swing.widgets.colours;



import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import peakaboo.common.Stderr;
import peakaboo.ui.swing.widgets.Spacing;



public class ComboTableCellRenderer implements ListCellRenderer, TableCellRenderer
{

	DefaultListCellRenderer		listRenderer	= new DefaultListCellRenderer();

	DefaultTableCellRenderer	tableRenderer	= new DefaultTableCellRenderer();


	private void configureRenderer(JLabel renderer, Object value)
	{
		renderer.setBorder(new MatteBorder(Spacing.iSmall(), Color.white));
		
		if ((value != null) && (value instanceof Color))
		{
			renderer.setIcon(new SquareIcon((Color) value));
			renderer.setText("");
			renderer.setBackground((Color)value);
		}
		else
		{
			renderer.setIcon(null);
			renderer.setText((String) value);
			renderer.setBackground(Color.yellow);
			Stderr.debug("ERROR");
		}
	}


	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		listRenderer = (DefaultListCellRenderer) listRenderer.getListCellRendererComponent(
			list,
			value,
			index,
			isSelected,
			cellHasFocus);
		
		configureRenderer(listRenderer, value);
		return listRenderer;
	}


	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		tableRenderer = (DefaultTableCellRenderer) tableRenderer.getTableCellRendererComponent(
			table,
			value,
			isSelected,
			hasFocus,
			row,
			column);
		
		configureRenderer(tableRenderer, value);
		return tableRenderer;
	}
}