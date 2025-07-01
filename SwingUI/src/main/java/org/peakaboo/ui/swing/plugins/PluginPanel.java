package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
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
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.plugins.browser.PluginRepositoryBrowser;
import org.peakaboo.ui.swing.plugins.manager.PluginManager;

public class PluginPanel extends HeaderLayer {

	JPanel details;
	
	LayerPanel parent;
	
	JButton browse, download;
	
	JPanel managerView;
	JPanel browserView;
	
	PluginsController controller;
	
	private static String noSelectionDescription = "Select a plugin or category from the sidebar to see information about available plugins.\n\nYou can add new plugins by dragging them into this window, or by using the 'Add' button in the toolbar";
	
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
		
		
		
		var tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab("Simple (Beta)", browserView);
		tabBuilder.addTab("Classic", managerView);
		setBody(tabBuilder.getBody());
		getHeader().setCentre(tabBuilder.getTabStrip());
		
		
		//header controls
	
		browse = new FluentButton()
				.withIcon(StockIcon.DOCUMENT_OPEN_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Open Plugins Folder")
				.withAction(this::browse);
		download = new FluentButton()
				.withIcon(StockIcon.GO_DOWN, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Get More Plugins")
				.withAction(this::download);
		
		ComponentStrip tools = new ComponentStrip(browse, download);
		
		getHeader().setRight(tools);
		//getHeader().setCentre("Manage Plugins");
		
	}
	

	


	
	

	public static boolean isPluginFile(File file) {
		boolean loadable = false;
		
		loadable |= DataSourceRegistry.system().isImportable(file);
		loadable |= DataSinkRegistry.system().isImportable(file);
		loadable |= FilterRegistry.system().isImportable(file);
		loadable |= MapFilterRegistry.system().isImportable(file);
		
		for (BoltPluginRegistry<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
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
	
	private void download() {
		DesktopApp.browser("https://github.com/nsherry4/PeakabooPlugins/releases/latest");
	}
	
	private void browseRepository() {
		setBody(browserView);
	}

	
	
	
}
