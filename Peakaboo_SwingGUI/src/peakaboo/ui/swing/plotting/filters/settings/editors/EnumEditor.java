package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Component;

import javax.swing.JComboBox;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class EnumEditor extends JComboBox
{

	public EnumEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		super((Enum<?>[]) param.possibleValues);
		
		setSelectedItem(param.getValue());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		addActionListener(new ParamListener(param, filter, controller, view));
	}
	
}
