package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import peakaboo.filters.AbstractFilter;

import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;


public class FilterEditButtonRenderer extends JPanel implements TableCellRenderer
{

	private ImageButton	edit;
	private JPanel noParams;


	public FilterEditButtonRenderer()
	{

		edit = new ImageButton(StockIcon.MISC_PREFERENCES, "…", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
		
		setLayout(new BorderLayout());
		add(edit, BorderLayout.CENTER);
		
		noParams = new JPanel();

	}


	public Component getTableCellRendererComponent(JTable table, Object _filter, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		
		AbstractFilter filter = (AbstractFilter)_filter;
		
		int numParameters = filter.getParameters().size();

		if (isSelected) {
			setOpaque(true);
			
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
			
		} else {
			setOpaque(false);

			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}


		if (table.getRowHeight() < this.getPreferredSize().height) {
			table.setRowHeight(this.getPreferredSize().height);
		}

		if (numParameters == 0)
		{
			
			if (isSelected) {
				noParams.setBackground(table.getSelectionBackground());
				noParams.setOpaque(true);
			} else {
				noParams.setOpaque(false);
			}
			
			return noParams;
		}
		return this;
	}
}
