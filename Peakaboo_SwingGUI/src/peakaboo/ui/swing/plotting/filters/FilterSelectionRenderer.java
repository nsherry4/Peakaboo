package peakaboo.ui.swing.plotting.filters;


import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.AbstractFilter.FilterType;
import swidget.widgets.Spacing;

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
			setText(f.getPluginName());

		} else if (value instanceof FilterType) {

			FilterType ft = (FilterType) value;
			setText(ft.toString());

		}

		return this;
	}

}
