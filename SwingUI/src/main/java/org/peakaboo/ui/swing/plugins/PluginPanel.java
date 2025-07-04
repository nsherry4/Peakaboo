package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.header.HeaderTabBuilder;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.plugins.browser.PluginRepositoryBrowser;
import org.peakaboo.ui.swing.plugins.manager.PluginManager;

public class PluginPanel extends HeaderLayer {

	private JPanel details;
	private LayerPanel parent;
	
	private PluginManager managerView;
	private PluginRepositoryBrowser browserView;
	
	private PluginsController controller;
	
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
		setBody(managerView);

		// Set up browser view	
		browserView = new PluginRepositoryBrowser(controller);		
		
		
		final String MANAGER_TITLE = "Classic"; 
		var tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab(MANAGER_TITLE, managerView);
		tabBuilder.addTab("Modern (Beta)", browserView);
		setBody(tabBuilder.getBody());
		getHeader().setCentre(tabBuilder.getTabStrip());
		
		// Listen for changes in the tab selection and update the header controls accordingly
		tabBuilder.getButtonGroup().getSelection().addItemListener(e -> {
			var buttons = new ArrayList<AbstractButton>();
			tabBuilder.getButtonGroup().getElements().asIterator().forEachRemaining(buttons::add);
			for (var button : buttons) {
				// Don't look at any buttons but the one with the MANAGER_TITLE
				if (! button.getText().equals(MANAGER_TITLE) ) continue;
				// If the button is selected, set the header controls to the manager view's controls
				// Otherwise, set it to the browser view's controls
				if (button.getModel().isSelected()) {
					getHeader().setLeft(managerView.getHeaderControls());
				} else {
					getHeader().setLeft(browserView.getHeaderControls());	
				}
			}
		});
				
		getHeader().setLeft(managerView.getHeaderControls());
		
		var browseButton = new FluentButton()
				.withIcon(StockIcon.DOCUMENT_OPEN_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Open Plugins Folder")
				.withAction(this::browse);
		getHeader().setRight(browseButton);
		
	}
	

	

	public static boolean isPluginFile(File file) {
		boolean loadable = false;
		
		for (BoltPluginRegistry<? extends BoltPlugin> manager : Tier.provider().getExtensionPoints().getRegistries()) {
			loadable |= manager.isImportable(file);
		}
		
		return loadable;
	}
	

	
	private void browse() {
		File appDataDir = DesktopApp.appDir("Plugins");
		appDataDir.mkdirs();
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(appDataDir);
		} catch (IOException e1) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to open plugin folder", e1);
		}
	}
	
	
}
