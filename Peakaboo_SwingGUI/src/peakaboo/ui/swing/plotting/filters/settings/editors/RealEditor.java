package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class RealEditor extends JSpinner
{

	public RealEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		setModel(new SpinnerNumberModel(param.realValue(), null, null, 0.1));
		getEditor().setPreferredSize(new Dimension(70, getEditor().getPreferredSize().height));
		setValue(param.realValue());
		
		addChangeListener(new ParamListener(param, filter, controller, view));

	}
	
}
