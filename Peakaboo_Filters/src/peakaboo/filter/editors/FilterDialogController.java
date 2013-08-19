package peakaboo.filter.editors;

import peakaboo.filter.model.AbstractFilter;
import autodialog.controller.AbstractADController;


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
