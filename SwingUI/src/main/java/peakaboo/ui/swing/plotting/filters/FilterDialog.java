package peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import autodialog.view.editors.AutoDialogButtons;
import autodialog.view.swing.SwingAutoDialog;
import autodialog.view.swing.editors.SwingEditorFactory;
import peakaboo.filter.controller.IFilteringController;
import peakaboo.filter.model.Filter;

public class FilterDialog extends SwingAutoDialog{
	
	static {
		SwingEditorFactory.registerStyleProvider("sub-filter", SubfilterEditor::new);
	}
	
	public FilterDialog(IFilteringController controller, Filter filter, AutoDialogButtons buttons, Window window) {
		super(filter.getParameterGroup(), buttons);
		
		getGroup().getValueHook().addListener(o -> {
			controller.filteredDataInvalidated();
		});
		
		
		setParent(window);
	}
	
	
	
}
