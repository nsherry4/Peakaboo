package org.peakaboo.ui.swing.plotting.filters;


import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonLayout;


class EditButtonRenderer implements TableCellRenderer {

	private FluentButton	edit;
	private JPanel container;


	public EditButtonRenderer() {

		edit = new FluentButton(StockIcon.MISC_PREFERENCES, IconSize.TOOLBAR_SMALL).withTooltip("Edit Filter").withLayout(FluentButtonLayout.IMAGE).withBordered(false);
		edit.setOpaque(false);
				
		container = new JPanel();
		container.setBorder(Spacing.bNone());

	}


	public Component getTableCellRendererComponent(
			JTable table, 
			Object filterObject, 
			boolean isSelected, 
			boolean hasFocus,
			int row, 
			int column) {
		
		Filter filter = (Filter)filterObject;
		
		int numParameters = filter.getParameters().size();

		if (table.getRowHeight() < container.getPreferredSize().height) {
			table.setRowHeight(container.getPreferredSize().height);
		}

		container.remove(edit);
		
		if (isSelected) {
			container.setBackground(table.getSelectionBackground());
			container.setOpaque(true);
		} else {
			container.setBackground(table.getBackground());
			container.setOpaque(false);
		}
		
		if (numParameters == 0)
		{
			return container;
		}
		container.add(edit);
		
		container.setToolTipText("Edit settings for this " + filter.getFilterName() + " Filter");
		
		return container;
	}
}
