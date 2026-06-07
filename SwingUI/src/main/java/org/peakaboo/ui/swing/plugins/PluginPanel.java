package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.plotting.PlotPanel;
import org.peakaboo.ui.swing.plotting.fitting.lookup.FilterBox;
import org.peakaboo.ui.swing.plugins.browser.PluginRepositoryBrowser;
import org.peakaboo.ui.swing.plugins.manager.PluginManager;

public class PluginPanel extends HeaderLayer {

	private JPanel details;
	private PlotPanel parent;
	
	private PluginManager managerView;
	private PluginRepositoryBrowser browserView;
	
	private PluginsController controller;
	
	public static interface HeaderControlProvider {
		ComponentStrip getHeaderControls();
	}
	
	public PluginPanel(PlotPanel parent) {
		super(parent, true);
		this.parent = parent;
		getContentRoot().setPreferredSize(new Dimension(850, 450));

		this.controller = new PluginsController(parent);
		
		// Set up manager view
		managerView = new PluginManager(this.controller);

		// Set up browser view
		browserView = new PluginRepositoryBrowser(controller);


		final String MANAGER_CARD = "Classic";
		final String BROWSER_CARD = "Modern";

		// The two views share a card layout; Modern is shown by default and Classic is
		// toggled on via the button in the right-hand toolbar.
		final CardLayout cards = new CardLayout();
		final JPanel body = new JPanel(cards);
		body.add(browserView, BROWSER_CARD);
		body.add(managerView, MANAGER_CARD);
		setBody(body);
		cards.show(body, BROWSER_CARD);

		// Centre search box filters the Modern list live (only shown for the Modern view)
		final FilterBox searchBox = new FilterBox(true);
		searchBox.setPreferredSize(new Dimension(260, searchBox.getPreferredSize().height));
		searchBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				browserView.setFilter(searchBox.getText());
			}
		});

		// Pad the centre so the search box doesn't crowd the left/right header sections
		final JPanel searchCentre = new JPanel(new BorderLayout());
		searchCentre.setOpaque(false);
		searchCentre.setBorder(new EmptyBorder(0, Spacing.huge*6, 0, Spacing.huge*6));
		searchCentre.add(searchBox, BorderLayout.CENTER);

		getHeader().setLeft(browserView.getHeaderControls());
		getHeader().setCentre(searchCentre);

		// Single toggle to switch to the (deprecated) Classic tree view
		var classicToggle = new FluentToggleButton()
				.withIcon(StockIcon.MENU_SETTINGS, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Advanced View")
				.withAction(selected -> {
					if (selected) {
						cards.show(body, MANAGER_CARD);
						getHeader().setLeft(managerView.getHeaderControls());
						getHeader().setCentre((Component) null);
					} else {
						cards.show(body, BROWSER_CARD);
						getHeader().setLeft(browserView.getHeaderControls());
						getHeader().setCentre(searchCentre);
					}
				});

		var browseButton = new FluentButton()
				.withIcon(StockIcon.DOCUMENT_OPEN_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Open Plugins Folder")
				.withAction(this::browse);
		var reloadButton = new FluentButton()
				.withIcon(StockIcon.ACTION_REFRESH_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Refresh Plugins")
				.withAction(controller::reload);
		getHeader().setRight(new ComponentStrip(classicToggle, reloadButton, browseButton));

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
			OneLog.log(Level.SEVERE, "Failed to open plugin folder", e1);
		}
	}
	
	
	@Override
	public void remove() {
		super.remove();
		parent.setWidgetsState();
	}
	
}
