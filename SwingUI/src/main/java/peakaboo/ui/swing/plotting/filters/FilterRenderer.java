package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.filter.model.Filter;

import swidget.Swidget;
import swidget.widgets.Spacing;

class FilterRenderer extends JPanel implements TableCellRenderer
{

	private JLabel	name;
	private JLabel	detail;


	public FilterRenderer()
	{

		name = new JLabel("Filter");
		detail = new JLabel("Detail");

		name.setFont(name.getFont().deriveFont(Font.BOLD));
		detail.setFont(detail.getFont().deriveFont(Font.PLAIN));

		setLayout(new BorderLayout());
		add(name, BorderLayout.CENTER);
		add(detail, BorderLayout.SOUTH);

		setBorder(Spacing.bSmall());

	}


	public Component getTableCellRendererComponent(JTable table, Object filter, boolean isSelected, boolean hasFocus,
			int row, int column)
	{

		Filter f = (Filter) filter;
		
		setToolTipText(Swidget.lineWrapHTML(this, f.getFilterDescription()));
		
		if (isSelected) {
			setBackground(table.getSelectionBackground());

			setForeground(table.getSelectionForeground());
			name.setForeground(table.getSelectionForeground());
			detail.setForeground(table.getSelectionForeground());

			setOpaque(true);
		} else {
			setOpaque(false);

			setForeground(table.getForeground());
			name.setForeground(table.getForeground());
			detail.setForeground(table.getForeground());


			setBackground(table.getBackground());
		}

		name.setText(f.getFilterName());
		detail.setText(f.getFilterType().toString());

		if (table.getRowHeight() < this.getPreferredSize().height) {
			table.setRowHeight(this.getPreferredSize().height);
		}

		return this;
	}
}
