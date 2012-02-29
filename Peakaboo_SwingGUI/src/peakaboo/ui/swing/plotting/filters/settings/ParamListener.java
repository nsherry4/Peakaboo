package peakaboo.ui.swing.plotting.filters.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.editors.BooleanEditor;
import peakaboo.ui.swing.plotting.filters.settings.editors.CodeEditor;
import peakaboo.ui.swing.plotting.filters.settings.editors.EnumEditor;
import peakaboo.ui.swing.plotting.filters.settings.editors.IntegerEditor;
import peakaboo.ui.swing.plotting.filters.settings.editors.RealEditor;
import peakaboo.ui.swing.plotting.filters.settings.editors.SubfilterEditor;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import eventful.EventfulTypeListener;

public class ParamListener implements ActionListener, ChangeListener, EventfulTypeListener<SubfilterEditor>
{

	private Parameter				param;
	private AbstractFilter 			filter;
	private IFilteringController 	controller;
	private SingleFilterView		view;


	public ParamListener(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		this.param = param;
		this.filter = filter;
		this.controller = controller;
		this.view = view;
	}


	public void actionPerformed(ActionEvent e)
	{

		update(e.getSource());
	}


	public void stateChanged(ChangeEvent e)
	{

		update(e.getSource());
	}


	public void update(Object source)
	{
		
		Object oldValue = param.getValue();

		switch (param.type)
		{
			case INTEGER:
				param.setValue(  ((IntegerEditor)source).getValue()  );
				break;
				
			case REAL:
				param.setValue(  ((RealEditor)source).getValue()  );
				break;
				
			case SET_ELEMENT:
				param.setValue(  ((EnumEditor)source).getSelectedItem()  );
				break;
				
			case BOOLEAN:
				param.setValue(  ((BooleanEditor)source).isSelected()  );
				break;
				
			case FILTER:
				param.setValue(  ((SubfilterEditor)source).getFilter()  );
				break;
				
			case SEPARATOR:
				break;
				
			case CODE:
				param.setValue( ((CodeEditor)source).codeEditor.getText() );
				break;
		}
		
		// if this input validates, signal the change, otherwise, reset
		// the value
		if (filter.validateParameters()) {
			
			controller.filteredDataInvalidated();
			
			view.updateWidgetsEnabled();
			
			
		} else {
			param.setValue(oldValue);

			// reset the control to the value of the parameter
			switch (param.type)
			{
				case INTEGER:
					((IntegerEditor) source).setValue(param.intValue());
					break;
					
				case REAL:
					((RealEditor) source).setValue(param.realValue());
					break;
					
				case SET_ELEMENT:
					((EnumEditor) source).setSelectedItem(param.getValue());
					break;
					
				case BOOLEAN:
					((BooleanEditor) source).setSelected(param.boolValue());
					break;
					
				case FILTER:
					((SubfilterEditor)source).setFilter(param.filterValue());
					break;
				
				case SEPARATOR:
					break;
				
				case CODE:
					JOptionPane.showMessageDialog(
							view, 
							param.getProperty("ErrorMessage"), 
							"Code Error", 
							JOptionPane.ERROR_MESSAGE,
							StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
						);
					break;
					
			}
			
		}



	}


	public void change(SubfilterEditor message)
	{
		update(message);
	}
	

}