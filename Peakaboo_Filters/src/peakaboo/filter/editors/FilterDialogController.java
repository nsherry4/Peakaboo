package peakaboo.filter.editors;

import autodialog.controller.AbstractADController;
import peakaboo.filter.model.AbstractFilter;


public abstract class FilterDialogController extends AbstractADController {

	private AbstractFilter filter;
	
	public FilterDialogController(AbstractFilter filter) {
		super(filter.getParameters().values());
		this.filter = filter;	
	}
	
	
	@Override
	public boolean validateParameters() {
		return filter.validateParameters();
	}

}
