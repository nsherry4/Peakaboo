package org.peakaboo.ui.swing.plotting.filters;


import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.ui.swing.app.PeakabooIcons;


class EditButtonRenderer implements TableCellRenderer {

	private FluentButton	edit;
	private JPanel container;

	private ImageIcon imgEdit, imgEditSelected;

	public EditButtonRenderer() {

		imgEdit = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON);
		imgEditSelected = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON, Color.WHITE);
		
		edit = new FluentButton(PeakabooIcons.MENU_SETTINGS, IconSize.BUTTON).withTooltip("Edit Filter").withLayout(FluentButtonLayout.IMAGE).withBordered(false);
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
			edit.setIcon(imgEditSelected);
		} else {
			container.setBackground(table.getBackground());
			container.setOpaque(false);
			edit.setIcon(imgEdit);
		}
		
		if (numParameters == 0)	{
			return container;
		}
		container.add(edit);
		
		container.setToolTipText("Edit settings for this " + filter.getFilterName() + " Filter");
		
		return container;
	}
}
