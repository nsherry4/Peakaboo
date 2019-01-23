package org.peakaboo.ui.swing.plotting.filters;

import java.awt.Window;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;

import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.SwingAutoDialog;
import net.sciencestudio.autodialog.view.swing.editors.SwingEditorFactory;

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
