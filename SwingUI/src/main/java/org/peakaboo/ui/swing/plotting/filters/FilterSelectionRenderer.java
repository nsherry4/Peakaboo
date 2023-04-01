package org.peakaboo.ui.swing.plotting.filters;


import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusText;

class FilterSelectionRenderer extends DefaultTreeCellRenderer {

	FilterSelectionRenderer() {
		super();
		setBorder(Spacing.bMedium());
	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		
		
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);


		if (value instanceof Filter) {

			Filter f = (Filter) value;
			setText(f.getFilterName());
			
			setToolTipText(StratusText.lineWrapHTML(this, f.getFilterDescription()));

		} else if (value instanceof FilterType) {

			FilterType ft = (FilterType) value;
			setText(ft.toString());
			
			setToolTipText(ft.getFilterTypeDescription());

		}

		return this;
	}

}
