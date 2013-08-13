package peakaboo.filter.editors;

import java.util.List;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.controller.IFilteringController;
import autodialog.controller.IAutoDialogController;
import autodialog.model.Parameter;
import fava.functionable.FList;

public class FilterDialogController implements IAutoDialogController {

	private AbstractFilter filter;
	private IFilterChangeListener listener;
	
	public FilterDialogController(AbstractFilter filter, IFilteringController controller) {
		this(filter, new FilterChangeListener(controller));
	}
	
	public FilterDialogController(AbstractFilter filter, IFilterChangeListener listener) {
		this.filter = filter;
		this.listener = listener;
	}
	
	
	@Override
	public boolean validateParameters() {
		return filter.validateParameters();
	}

	@Override
	public void parametersUpdated() {
		listener.change();		
	}

	@Override
	public List<Parameter<?>> getParameters() {
		return new FList<>(filter.getParameters().values());
	}

	@Override
	public void submit() {}

	@Override
	public void cancel() {}

	@Override
	public void close() {}

}
