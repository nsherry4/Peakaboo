package peakaboo.ui.swing.plotting.filters.settings.editors;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class EditorFactory
{

	public static JComponent createEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		JComponent component = null;
		//generate the control which will display the value for this filter
		switch (param.type)
		{
			case INTEGER:

				component = new IntegerEditor(param, filter, controller, view);
				break;

			case REAL:

				component = new RealEditor(param, filter, controller, view);
				break;
				
			case SET_ELEMENT:
			
				component = new EnumEditor(param, filter, controller, view);
				break;

			case BOOLEAN:
				
				component = new BooleanEditor(param, filter, controller, view);
				break;
				
			case FILTER:
									
				component = new SubfilterEditor(param, filter, controller, view);
				break;
				
			case SEPARATOR:
				
				component = new JSeparator(JSeparator.HORIZONTAL);
				break;
				
			case CODE:
						
				component = new CodeEditor(param, filter, controller, view);
				break;
				
		}
		
		return component;
		
	}
	
}
