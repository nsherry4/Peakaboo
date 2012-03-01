package peakaboo.ui.swing.plotting.filters.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.editors.Editor;
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

		update((Editor)e.getSource());
	}


	public void stateChanged(ChangeEvent e)
	{

		update((Editor)e.getSource());
	}


	public void update(Editor editor)
	{
		
		Object oldValue = param.getValue();
		param.setValue(editor.getEditorValue());
		
		// if this input validates, signal the change, otherwise, reset
		// the value
		if (filter.validateParameters()) {
			
			controller.filteredDataInvalidated();
			view.updateWidgetsEnabled();
			
		} else {
			// reset the parameter and the editor to the old value of the parameter
			param.setValue(oldValue);
			
				
			switch (param.type)
			{
				
				case CODE:
					JOptionPane.showMessageDialog(
							view, 
							param.getProperty("ErrorMessage"), 
							"Code Error", 
							JOptionPane.ERROR_MESSAGE,
							StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
						);
					break;
					
				default:
					editor.setFromParameter();
					
			}
			
		}



	}


	public void change(SubfilterEditor message)
	{
		update(message);
	}
	

}