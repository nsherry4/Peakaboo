package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Dimension;

import javax.swing.JSpinner;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class IntegerEditor extends JSpinner
{

	public IntegerEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		getEditor().setPreferredSize(new Dimension(70, getEditor().getPreferredSize().height));
		setValue(param.intValue());

		addChangeListener(new ParamListener(param, filter, controller, view));
	}
	
}
