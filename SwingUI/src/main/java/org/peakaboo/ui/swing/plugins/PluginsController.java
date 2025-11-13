package org.peakaboo.ui.swing.plugins;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.repository.AggregatePluginRepository;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.eventful.EventfulBeacon;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.tier.Tier;

public class PluginsController extends EventfulBeacon {
	
	private LayerPanel parentLayer;
	private AggregatePluginRepository aggregateRepo = Tier.provider().getPluginRepositories();
	
	public AggregatePluginRepository getRepository() {
		return aggregateRepo;
	}

	public LayerPanel getParentLayer() {
		return parentLayer;
	}

	public PluginsController(LayerPanel parent) {
		this.parentLayer = parent;
	}

	
	/**
	 * Try adding a jar to a specific plugin manager. Return true if the given manager accepted the jar
	 */
	private boolean addFileToManager(File file, BoltPluginRegistry<? extends BoltPlugin> manager, boolean silent) throws BoltImportException {
		
		if (!manager.isImportable(file)) {
			return false;
		}
		
		BoltContainer<? extends BoltPlugin> container = manager.importOrUpgradeFile(file);
		
		this.reload();
		if (!silent) new LayerDialog("Imported New Plugins", descriptorsToHTML(container.getPlugins())).showIn(parentLayer);

		return true;
	}
	
	
	public void addFromFilesystem() {
		StratusFilePanels.openFile(parentLayer, "Import Plugins", Env.homeDirectory(), new SimpleFileExtension("Peakaboo Plugin", "jar"), result -> {
			if (!result.isPresent()) {
				return;
			}
			
			install(result.get());

		});
	}
	
	public void install(File file) {
		install(file, false);
	}
	
	/**
	 * Add a jar file containing plugins
	 */
	public void install(File file, boolean silent) {
		
		boolean handled = false;
		
		try {

			for (BoltPluginRegistry<? extends BoltPlugin> manager : Tier.provider().getExtensionPoints().getRegistries()) {
				handled |= addFileToManager(file, manager, silent);
			}
			
		} catch (BoltImportException e) {
		
			PeakabooLog.get().log(Level.WARNING, e.getMessage(), e);
			new LayerDialog(
					"Import Failed", 
					"Peakboo was unable to import the plugin\n" + e.getMessage(), 
					StockIcon.BADGE_ERROR).showIn(parentLayer);
			handled = true;
		}
		
		if (!handled) {
			new LayerDialog(
					"No Plugins Found", 
					"Peakboo could not fint any plugins in the file(s) provided", 
					StockIcon.BADGE_ERROR).showIn(parentLayer);
		}
		
		reload();

	}
	
	public void remove(PluginDescriptor<BoltPlugin> plugin) {
		remove(plugin, false);
	}
	
	public void remove(PluginDescriptor<BoltPlugin> plugin, boolean silent) {
		/*
		 * This is a little tricky. There's no rule that says that each plugin is in 
		 * it's own jar file. We need to confirm with the user that they want to 
		 * remove the jar file and all plugins that it contains.
		 */
		
		if (plugin == null) {
			// No need to do anything
			return;
		}
		
		BoltContainer<? extends BoltPlugin> container = plugin.getContainer();
		if (!container.isDeletable()) {
			return;
		}
		
		Runnable action = () -> {
			container.delete();
			this.reload();
		};
		
		if (silent) {
			action.run();
		} else {
			new LayerDialog("Delete Plugin Bundle?", descriptorsToHTML(container.getPlugins()))
				.addRight(
					new FluentButton("Delete")
						.withAction(action)
						.withStateCritical()
					)
				.addLeft(new FluentButton("Cancel"))
				.showIn(parentLayer);
		}
		
		
	}
	
	public void reload() {

		for (var manager : Tier.provider().getExtensionPoints().getRegistries()) {
			manager.reload();
		}
		
		aggregateRepo.refresh();
		
		this.updateListeners();
	}

	/**
	 * Upgrade a plugin from an already-downloaded file. This method performs the validation and upgrade process
	 * without downloading, allowing the caller to handle the download asynchronously if needed.
	 *
	 * @param plugin The plugin to be upgraded
	 * @param meta The metadata for the upgrade
	 * @param upgradeFile The already-downloaded plugin file
	 * @param silent Whether to show UI dialogs
	 */
	public void upgradeFromFile(PluginDescriptor<BoltPlugin> plugin, PluginMetadata meta, File upgradeFile, boolean silent) {
		// Confirm that the plugin is actually an upgrade for the one we have
		if (! meta.isUpgradeFor(plugin)) {
			showError("Upgrade Error", "The listed plugin is not a valid upgrade.");
			return;
		}

		// Remove old plugin and install new one
		remove(plugin, silent);
		install(upgradeFile, silent);
	}

	/**
	 * We need to determine if the new plugin described by meta is actually a newer plugin for the one given.
	 * Then we need to remove the old one and install the new one. We should try to minimize the chances that the old plugin
	 * is removed without the new one being installed properly.
	 *
	 * Note: This method performs a blocking download. UI code should use upgradeFromFile() with async download instead.
	 **/
	public void upgrade(PluginDescriptor<BoltPlugin> plugin, PluginMetadata meta, boolean silent) {
		// Confirm that the plugin is actually an upgrade for the one we have
		if (! meta.isUpgradeFor(plugin)) {
			showError("Upgrade Error", "The listed plugin is not a valid upgrade.");
			return;
		}

		// Download the new plugin file
		// NB this will need to be changed if we want to support repositories which don't use unauthenticated HTTP for downloads
		try {
			File upgrade = meta.download().get();
			upgradeFromFile(plugin, meta, upgrade, silent);
		} catch (NoSuchElementException ex) {
			showError("Upgrade Error", "Failed to download upgrade: " + ex.getMessage());
		}

	}
	
    public void fixIssue(BoltIssue<? extends BoltPlugin> issue) {
    	issue.fix();
    	reload();
    }

	public String descriptorsToHTML(List<?> stuff) {
		
		StringBuilder buff = new StringBuilder();
		for (Object o : stuff) {
			if (o instanceof PluginDescriptor<?> plugin) {
				if (!buff.isEmpty()) {
					buff.append("<br>");
				}
				buff.append(descriptorToHTML(plugin));
			} else {
				throw new RuntimeException("Item was not a plugin descriptor");
			}
		}
		return buff.toString();
		
	}
	
	public String descriptorToHTML(PluginDescriptor<?> plugin) {
		// The details panel is just a convenient component which hasn't had its default font properties changes
		String wrappedDescription = StratusText.lineWrapHTMLInline(getParentLayer(), plugin.getDescription(), 400);
		return String.format("<div style='padding: 5px;'><div style='font-size: 20pt;'>%s</div><div style='font-size: 10pt; padding-bottom: 5px;'>version %s</div><div style=''>%s</div></div>", plugin.getName(), plugin.getVersion(), wrappedDescription);
	}
	
	public void showError(String message) {
		showError("Error", message);
	}
	public void showError(String title, String message) {
		new LayerDialog(title, message, StockIcon.BADGE_ERROR).showIn(parentLayer);
	}
	
}
