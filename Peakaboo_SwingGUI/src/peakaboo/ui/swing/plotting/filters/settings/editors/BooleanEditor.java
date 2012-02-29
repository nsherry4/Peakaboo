package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Component;

import javax.swing.JCheckBox;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class BooleanEditor extends JCheckBox
{

	public BooleanEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		setSelected(param.boolValue());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setOpaque(false);
		
		addChangeListener(new ParamListener(param, filter, controller, view));
	}
	
}
