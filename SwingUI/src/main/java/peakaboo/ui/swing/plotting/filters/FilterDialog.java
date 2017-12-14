package peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import autodialog.model.Parameter;
import autodialog.view.swing.AutoDialog;
import autodialog.view.swing.editors.SwingEditorFactory;
import peakaboo.filter.controller.IFilteringController;
import peakaboo.filter.model.Filter;

public class FilterDialog extends AutoDialog{
	
	static {
		SwingEditorFactory.registerStyleProvider("sub-filter", SubfilterEditor::new);
	}
	
	public FilterDialog(final IFilteringController controller, Filter filter, AutoDialogButtons buttons, Window window) {
		super(new FilterDialogController(filter){

			@Override
			public void parameterUpdated(Parameter<?> param) {
				controller.filteredDataInvalidated();		
			}}, buttons, window);
		
		setTitle(filter.getFilterName());
	}
	
	
	
}
