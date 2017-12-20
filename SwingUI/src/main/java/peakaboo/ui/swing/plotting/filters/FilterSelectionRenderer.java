package peakaboo.ui.swing.plotting.filters;


import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterType;
import swidget.widgets.Spacing;
import swidget.widgets.TextWrapping;

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


		if (value instanceof Filter) {

			Filter f = (Filter) value;
			setText(f.getFilterName());
			
			setToolTipText(TextWrapping.wrapTextForMultiline(f.getFilterDescription()));

		} else if (value instanceof FilterType) {

			FilterType ft = (FilterType) value;
			setText(ft.toString());
			
			setToolTipText(ft.getFilterTypeDescription());

		}

		return this;
	}

}
