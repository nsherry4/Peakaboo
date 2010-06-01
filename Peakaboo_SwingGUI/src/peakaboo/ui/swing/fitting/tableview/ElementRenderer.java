package peakaboo.ui.swing.fitting.tableview;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import peakaboo.datatypes.peaktable.Element;
import peakaboo.ui.swing.widgets.Spacing;

public class ElementRenderer extends JPanel implements TableCellRenderer
{

	private JLabel	name;
	private JLabel	detail;


	public ElementRenderer()
	{

		name = new JLabel("Element");
		detail = new JLabel("Detail");

		name.setFont(name.getFont().deriveFont(name.getFont().getSize() * 1.4f));
		detail.setFont(detail.getFont().deriveFont(Font.PLAIN));

		setLayout(new BorderLayout());
		add(name, BorderLayout.CENTER);
		add(detail, BorderLayout.SOUTH);

		setBorder(Spacing.bSmall());

	}


	public Component getTableCellRendererComponent(JTable table, Object element, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		Element e = (Element) element;

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

		name.setText(e.toString());
		detail.setText("Symbol: " + e.name() + ", Atomic #: " + (e.ordinal() + 1));

		if (table.getRowHeight() < this.getPreferredSize().height) {
			table.setRowHeight(this.getPreferredSize().height);
		}

		return this;
	}
}
