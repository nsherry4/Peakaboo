package peakaboo.filter.editors;

import peakaboo.filter.controller.IFilteringController;

public class FilterChangeListener implements IFilterChangeListener
{
	private IFilteringController controller;
	
	public FilterChangeListener(IFilteringController controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void change()
	{
		controller.filteredDataInvalidated();
	}
}
