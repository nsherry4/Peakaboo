package peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.SwingAutoDialog;
import net.sciencestudio.autodialog.view.swing.editors.SwingEditorFactory;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.filter.model.Filter;

class FilterDialog extends SwingAutoDialog{
	
	static {
		SwingEditorFactory.registerStyleProvider("sub-filter", SubfilterEditor::new);
	}
	
	FilterDialog(FilteringController controller, Filter filter, AutoDialogButtons buttons, Window window) {
		super(window, filter.getParameterGroup(), buttons);
		
		getGroup().getValueHook().addListener(o -> {
			controller.filteredDataInvalidated();
		});
		
	}
	
	
	
}
