package org.peakaboo.ui.swing.plotting.filters;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.autodialog.view.swing.editors.SwingEditorFactory;
import org.peakaboo.framework.autodialog.view.swing.layouts.NarrowSwingLayout;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.FluentLabel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;

class FilterSettingsPanel extends ClearPanel {
	
	static {
		SwingEditorFactory.registerStyleProvider("sub-filter", SubfilterEditor::new);
	}
	
	private static Map<Filter, JComponent> editorUIs = new HashMap<>();
	
	private FilteringController controller;
	private FiltersetViewer parentComponent;
	
	public FilterSettingsPanel(FilteringController controller, FiltersetViewer parent) {
		this.controller = controller;
		this.parentComponent = parent;
		
		this.setBorder(Spacing.bSmall());
		this.setLayout(new BorderLayout());
		
	}

	public void setFilter(Filter filter) {
		this.removeAll();
		
		JComponent component;
		if (!editorUIs.containsKey(filter)) {
			var group = filter.getParameterGroup();
			var layout =  new NarrowSwingLayout(200);
			layout.initialize(group);
			var autopanel = new SwingAutoPanel(group, true, layout); 
			autopanel.setBorder(Spacing.bSmall());
			//Hook up our Parameter's event system to Peakaboo's
			group.getValueHook().addListener(o -> {
				controller.filtersUpdated();
				controller.filteredDataInvalidated();
			});
			//Wrap for layout
			component = new ClearPanel(new BorderLayout());
			component.add(autopanel, BorderLayout.CENTER);
			
			//Stash for later use
			editorUIs.put(filter, component);
		}
		component = editorUIs.get(filter);

		var titlepanel = new TitleBar(this.parentComponent, filter);

		this.add(titlepanel, BorderLayout.NORTH);
		this.add(component, BorderLayout.CENTER);
		
	}
	
	
	private static class TitleBar extends ClearPanel {
		public TitleBar(FiltersetViewer parent, Filter filter) {
			var label = new JLabel(filter.getFilterName());
			label.setOpaque(true);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(label.getFont().deriveFont(13f).deriveFont(Font.BOLD));
			label.setBorder(Spacing.bSmall());
			
			var help = new FluentLabel()
					.withIcon(StockIcon.APP_HELP, IconSize.BUTTON, Stratus.getTheme().getControlText())
					.withBorder(Spacing.bMedium())
					.withTooltip(filter.getFilterName() + ": " + filter.getFilterDescription());		
			help.setFocusable(false);
			
			var back = new FluentButton()
					.withIcon(StockIcon.GO_PREVIOUS, Stratus.getTheme().getControlText())
					.withTooltip("Return to filter list")
					.withBordered(false)
					.withBorder(Spacing.bMedium())
					.withAction(() -> parent.showEditPane());
			
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(back);
			this.add(label);
			this.add(help);
		}
	}
	

}
