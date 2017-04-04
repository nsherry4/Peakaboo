package peakaboo.filter.editors;

import autodialog.controller.AbstractADController;
import peakaboo.filter.model.Filter;


public abstract class FilterDialogController extends AbstractADController {

	private Filter filter;
	
	public FilterDialogController(Filter filter) {
		super(filter.getParameters().values());
		this.filter = filter;	
	}
	
	
	@Override
	public boolean validateParameters() {
		return filter.validateParameters();
	}

}
