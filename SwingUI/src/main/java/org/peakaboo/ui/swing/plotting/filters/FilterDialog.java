package org.peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.autodialog.view.editors.AutoDialogButtons;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoDialog;
import org.peakaboo.framework.autodialog.view.swing.editors.SwingEditorFactory;

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
