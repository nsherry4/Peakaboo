package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class EnumEditor extends JComboBox implements Editor
{

	private Parameter param;
	
	public EnumEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		super((Enum<?>[]) param.possibleValues);
	
		this.param = param;
		
		setSelectedItem(param.getValue());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		addActionListener(new ParamListener(param, filter, controller, view));
	}

	@Override
	public boolean expandVertical()
	{
		return false;
	}

	@Override
	public boolean expandHorizontal()
	{
		return false;
	}

	@Override
	public Style getStyle()
	{
		return Style.LABEL_ON_SIDE;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setFromParameter()
	{
		setSelectedItem(param.<Enum>enumValue());
	}

	@Override
	public Object getEditorValue()
	{
		return getSelectedItem();
	}
	
	
}
