package peakaboo.ui.swing.mapping.colours;



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

import peakaboo.common.Version;
import peakaboo.mapping.colours.OverlayColour;
import peakaboo.mapping.colours.RatioColour;
import swidget.widgets.Spacing;



public class ComboTableCellRenderer<T> implements ListCellRenderer<T>, TableCellRenderer
{

	private DefaultListCellRenderer	listRenderer = new DefaultListCellRenderer();
	private DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();


	private void configureRenderer(JLabel renderer, Object value)
	{
		renderer.setBorder(new MatteBorder(Spacing.iSmall(), Color.white));
		
		if ((value != null) && (value instanceof OverlayColour))
		{
			//Overlay Mode
			Color c = ((OverlayColour) value).toColor();
			renderer.setIcon(new SquareIcon( c ));
			renderer.setText("");
			renderer.setBackground( c );
		} 
		else if (value != null && value instanceof Integer) 
		{
			//Ratio Mode
			Color c = RatioColour.values()[((Integer)value) - 1].toColor();
			renderer.setIcon(new SquareIcon( c ));
			renderer.setText("");
			renderer.setBackground( c );
		}
		else
		{
			renderer.setIcon(null);
			renderer.setText((String) value);
			renderer.setBackground(Color.yellow);
			if (!Version.release) System.err.println("ERROR");
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