package peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import autodialog.model.Parameter;
import autodialog.view.AutoDialog;
import peakaboo.filter.controller.IFilteringController;
import peakaboo.filter.editors.FilterDialogController;
import peakaboo.filter.model.Filter;

public class FilterDialog extends AutoDialog{
	
	public FilterDialog(final IFilteringController controller, Filter filter, AutoDialogButtons buttons, Window window) {
		super(new FilterDialogController(filter){

			@Override
			public void parameterUpdated(Parameter<?> param) {
				controller.filteredDataInvalidated();		
			}}, buttons, window);
		
		setTitle(filter.getFilterName());
	}
	
	
	
}
