package peakaboo.ui.swing.plotting.filters.settings.editors;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

public class EditorFactory
{

	public static Editor createEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		Editor editor = null;
		//generate the control which will display the value for this filter
		switch (param.type)
		{
			case INTEGER:

				editor = new IntegerEditor(param, filter, controller, view);
				break;

			case REAL:

				editor = new RealEditor(param, filter, controller, view);
				break;
				
			case SET_ELEMENT:
			
				editor = new EnumEditor(param, filter, controller, view);
				break;

			case BOOLEAN:
				
				editor = new BooleanEditor(param, filter, controller, view);
				break;
				
			case FILTER:
									
				editor = new SubfilterEditor(param, filter, controller, view);
				break;
				
			case SEPARATOR:
				
				editor = new SeparatorEditor();
				break;
				
			case CODE:
						
				editor = new CodeEditor(param, filter, controller, view);
				break;
				
		}
		
		return editor;
		
	}
	
}
