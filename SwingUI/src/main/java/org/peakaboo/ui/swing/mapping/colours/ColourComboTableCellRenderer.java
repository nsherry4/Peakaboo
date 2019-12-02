package org.peakaboo.ui.swing.mapping.colours;



import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.ratio.RatioColour;
import org.peakaboo.framework.swidget.widgets.Spacing;



public class ColourComboTableCellRenderer<T> implements ListCellRenderer<T>, TableCellRenderer
{

	private DefaultListCellRenderer	listRenderer = new DefaultListCellRenderer();
	private DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();


	private void configureRenderer(JLabel renderer, Object value)
	{
		renderer.setBorder(new MatteBorder(Spacing.iSmall(), Color.white));
		
		if ((value instanceof OverlayColour))
		{
			//Overlay Mode
			Color c = new Color(((OverlayColour) value).toARGB());
			renderer.setIcon(new ColourRenderer( c ));
			renderer.setText("");
			renderer.setBackground( c );
		} 
		else if (value instanceof Integer) 
		{
			//Ratio Mode
			Color c = new Color(RatioColour.values()[((Integer)value) - 1].toARGB(), true);
			renderer.setIcon(new ColourRenderer( c ));
			renderer.setText("");
			renderer.setBackground( c );
		}
		else
		{
			renderer.setIcon(null);
			renderer.setText((String) value);
			renderer.setBackground(Color.yellow);
			PeakabooLog.get().log(Level.WARNING, "Problem determining mode for " + value);
		}
	}

	//For combobox editor
	@Override
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


	//For table renderer
	@Override
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