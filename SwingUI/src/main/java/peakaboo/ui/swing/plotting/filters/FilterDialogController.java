package peakaboo.ui.swing.plotting.filters;

import autodialog.controller.AbstractADController;
import autodialog.view.swing.editors.SwingEditorFactory;
import peakaboo.filter.model.Filter;


public abstract class FilterDialogController extends AbstractADController {

	private Filter filter;
	
	public FilterDialogController(Filter filter) {
		super(SwingEditorFactory.forParameters(filter.getParameters().values()));
		this.filter = filter;	
	}
	
	
	@Override
	public boolean validate() {
		return filter.validateParameters();
	}

}
