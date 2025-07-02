package org.peakaboo.ui.swing.plugins;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.repository.AggregatingPluginRepository;
import org.peakaboo.framework.bolt.repository.HttpsPluginRepository;
import org.peakaboo.framework.bolt.repository.LocalPluginRepository;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.PluginRepository;
import org.peakaboo.framework.eventful.EventfulBeacon;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;

public class PluginsController extends EventfulBeacon {
	
	private LayerPanel parentLayer;
	private AggregatingPluginRepository aggregateRepo = new AggregatingPluginRepository(
			new HttpsPluginRepository("https://github.com/PeakabooLabs/peakaboo-plugins/releases/download/600/"),
			new LocalPluginRepository(DataSourceRegistry.system()),
			new LocalPluginRepository(DataSinkRegistry.system())
		);
	
	public AggregatingPluginRepository getRepository() {
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
	private boolean addFileToManager(File file, BoltPluginRegistry<? extends BoltPlugin> manager) throws BoltImportException {
		
		if (!manager.isImportable(file)) {
			return false;
		}
		
		BoltContainer<? extends BoltPlugin> container = manager.importOrUpgradeFile(file);
		
		this.reload();
		new LayerDialog("Imported New Plugins", descriptorsToHTML(container.getPlugins())).showIn(parentLayer);

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
	
	/**
	 * Add a jar file containing plugins
	 */
	public void install(File file) {
		
		boolean handled = false;
		
		try {
			handled |= addFileToManager(file, DataSourceRegistry.system());
			handled |= addFileToManager(file, DataSinkRegistry.system());
			handled |= addFileToManager(file, FilterRegistry.system());
			handled |= addFileToManager(file, MapFilterRegistry.system());
			
			for (BoltPluginRegistry<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
				handled |= addFileToManager(file, manager);
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
	
	/**
	 * Downloads a plugin from an InputStream, typically from a repository.
	 * This will save the stream to a temporary file which will be deleted on exit.
	 * @param stream the InputStream containing the plugin data
	 * @return a File object pointing to the temporary file containing the downloaded plugin
	 */
	public File download(InputStream stream) {
		
		// Store it in a temp file so we can import in into a plugin registry
        File tempFile = null;
        try {
            tempFile = File.createTempFile("plugin_", ".jar");
            tempFile.deleteOnExit();
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(getParentLayer(), "Failed to save plugin: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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
		
		if (container.isEmpty()) {
			return;
		}
		
		Runnable action = () -> {
			plugin.getContainer().delete();
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
		
		// We only refresh the registries which we show in this UI
		DataSourceRegistry.system().reload();
		DataSinkRegistry.system().reload();
		FilterRegistry.system().reload();
		MapFilterRegistry.system().reload();
		
		
		for (var manager : Tier.provider().getPluginManagers()) {
			manager.reload();
		}
		
		this.updateListeners();
	}

	public void upgrade(PluginDescriptor<BoltPlugin> plugin, PluginMetadata meta) {
		upgrade(plugin, meta, false);
	}
	
	/**
	 * We need to determine if the new plugin described by meta is actually a newer plugin for the one given.
	 * Then we need to remove the old one and install the new one. We should try to minimize the chances that the old plugin 
	 * is removed without the new one being installed properly.
	 **/
	public void upgrade(PluginDescriptor<BoltPlugin> plugin, PluginMetadata meta, boolean silent) {	
		// Confirm that the plugin is actually an upgrade for the one we have
		if (! meta.isUpgradeFor(plugin)) {
			JOptionPane.showMessageDialog(getParentLayer(), "The plugin you are trying to upgrade is not compatible with the new version.", "Upgrade Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Download the new plugin file
		// NB this will need to be changed if we want to support repositories which don't use unauthenticated HTTP for downloads 
        InputStream downloadStream = PluginRepository.downloadPluginHttp(meta);
        File upgrade = download(downloadStream);
        if (upgrade == null) {
			JOptionPane.showMessageDialog(getParentLayer(), "Failed to download the plugin for upgrade.", "Upgrade Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
        remove(plugin, silent);
        install(upgrade);
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

	public Optional<PluginRepository> getRepositoryForPlugin(PluginMetadata plugin) {
		return aggregateRepo.findRepositoryForPlugin(plugin);
	}
	
	
}
