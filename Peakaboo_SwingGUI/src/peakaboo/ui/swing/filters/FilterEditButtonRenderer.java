package peakaboo.ui.swing.filters;


import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.ImageButton.Layout;

public class FilterEditButtonRenderer extends JPanel implements TableCellRenderer
{

	private ImageButton	edit;


	public FilterEditButtonRenderer()
	{

		edit = new ImageButton("preferences", "…", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
		
		setLayout(new BorderLayout());
		add(edit, BorderLayout.CENTER);

	}


	public Component getTableCellRendererComponent(JTable table, Object filter, boolean isSelected, boolean hasFocus,
			int row, int column)
	{

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

		return this;
	}
}
