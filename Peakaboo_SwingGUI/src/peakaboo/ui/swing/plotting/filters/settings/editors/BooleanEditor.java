package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class BooleanEditor extends JCheckBox implements Editor
{
	
	Parameter param;

	public BooleanEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		this.param = param;
		
		setSelected(param.boolValue());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setOpaque(false);
		
		addChangeListener(new ParamListener(param, filter, controller, view));
	}
	
	@Override
	public boolean expandVertical()
	{
		return false;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
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
	public void setFromParameter()
	{
		setSelected(param.boolValue());
	}

	@Override
	public Object getEditorValue()
	{
		return isSelected();
	}
	
}
