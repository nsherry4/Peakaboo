package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.header.HeaderTabBuilder;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.plugins.browser.PluginRepositoryBrowser;
import org.peakaboo.ui.swing.plugins.manager.PluginManager;

public class PluginPanel extends HeaderLayer {

	JPanel details;
	
	LayerPanel parent;
	
	JButton browse, download;
	
	PluginManager managerView;
	PluginRepositoryBrowser browserView;
	
	PluginsController controller;
	
	private static String noSelectionDescription = "Select a plugin or category from the sidebar to see information about available plugins.\n\nYou can add new plugins by dragging them into this window, or by using the 'Add' button in the toolbar";
	
	public static interface HeaderControlProvider {
		ComponentStrip getHeaderControls();
	}
	
	public PluginPanel(LayerPanel parent) {
		super(parent, true);
		this.parent = parent;
		getContentRoot().setPreferredSize(new Dimension(850, 400));

		this.controller = new PluginsController(parent);
		
		// Set up manager view
		managerView = new PluginManager(this.controller);
		details = new JPanel(new BorderLayout());
		details.add(new BlankMessagePanel("No Selection", noSelectionDescription), BorderLayout.CENTER);

		setBody(managerView);

		// Set up browser view	
		browserView = new PluginRepositoryBrowser(controller);		
		
		
		final String MANAGER_TITLE = "Classic"; 
		var tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab(MANAGER_TITLE, managerView);
		tabBuilder.addTab("Simple (Beta)", browserView);
		setBody(tabBuilder.getBody());
		getHeader().setCentre(tabBuilder.getTabStrip());
		
		// Listen for changes in the tab selection and update the header controls accordingly
		tabBuilder.getButtonGroup().getSelection().addChangeListener(e -> {
			var buttons = new ArrayList<AbstractButton>();
			tabBuilder.getButtonGroup().getElements().asIterator().forEachRemaining(buttons::add);
			for (var button : buttons) {
				// Don't look at any buttons but the one with the MANAGER_TITLE
				if (! button.getText().equals(MANAGER_TITLE) ) continue;
				// If the button is selected, set the header controls to the manager view's controls
				// Otherwise, set it to the browser view's controls
				if (button.getModel().isSelected()) {
					getHeader().setRight(managerView.getHeaderControls());
				} else {
					getHeader().setRight(browserView.getHeaderControls());	
				}
			}
		});
				
		getHeader().setRight(managerView.getHeaderControls());
		
	}
	

	

	public static boolean isPluginFile(File file) {
		boolean loadable = false;
		
		for (BoltPluginRegistry<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
			loadable |= manager.isImportable(file);
		}
		
		return loadable;
	}
	


	
	private void browseRepository() {
		setBody(browserView);
	}

	
	
	
}
