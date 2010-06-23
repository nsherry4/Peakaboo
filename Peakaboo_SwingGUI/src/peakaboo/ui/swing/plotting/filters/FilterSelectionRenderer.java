package peakaboo.ui.swing.plotting.filters;


import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.filters.AbstractFilter;
import peakaboo.filters.AbstractFilter.FilterType;
import peakaboo.ui.swing.widgets.Spacing;

public class FilterSelectionRenderer extends DefaultTreeCellRenderer
{

	public FilterSelectionRenderer()
	{
		super();
		setBorder(Spacing.bSmall());
	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);


		if (value instanceof AbstractFilter) {

			AbstractFilter f = (AbstractFilter) value;
			setText(f.getFilterName());

		} else if (value instanceof FilterType) {

			FilterType ft = (FilterType) value;
			setText(ft.toString());

		}

		return this;
	}

}
