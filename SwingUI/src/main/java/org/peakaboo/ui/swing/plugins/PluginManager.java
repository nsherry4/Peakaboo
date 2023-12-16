package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.sink.plugin.DataSinkPluginManager;
import org.peakaboo.dataset.source.plugin.DataSourcePluginManager;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.FileDrop;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.stencil.StencilTreeCellRenderer;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.mapping.filter.model.MapFilterPluginManager;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.app.DesktopApp;

public class PluginManager extends HeaderLayer {

	JPanel details;
	JTree tree;
	LayerPanel parent;
	
	JButton add, remove, reload, browse, download;
	
	private static String noSelectionDescription = "Select a plugin or category from the sidebar to see information about available plugins.\n\nYou can add new plugins by dragging them into this window, or by using the 'Add' button in the toolbar";
	
	public PluginManager(LayerPanel parent) {
		super(parent, true);
		this.parent = parent;
		getContentRoot().setPreferredSize(new Dimension(850, 400));
				
		//body
		JPanel body = new JPanel(new BorderLayout());
		body.add(pluginTree(), BorderLayout.WEST);
		details = new JPanel(new BorderLayout());
		details.add(new BlankMessagePanel("No Selection", noSelectionDescription), BorderLayout.CENTER);
		body.add(details, BorderLayout.CENTER);
		new FileDrop(body, new FileDrop.Listener() {
			
			@Override
			public void urlsDropped(URL[] urls) {
				
				TaskMonitor<List<File>> monitor = Peakaboo.getUrlsAsync(Arrays.asList(urls), optfiles -> {
					if (!optfiles.isPresent()) {
						return;
					}
					for (File file : optfiles.get()) {
						addPluginFile(file);
					}
				});
				
				TaskMonitorPanel.onLayerPanel(monitor, parent);

			}
			
			@Override
			public void filesDropped(File[] files) {
				for (File file : files) {
					addPluginFile(file);
				}
			}
		});

		setBody(body);
		
		
		//header controls
		add = new FluentButton(StockIcon.EDIT_ADD).withBordered(false).withButtonSize(FluentButtonSize.LARGE).withTooltip("Import Plugins").withAction(this::add);
		remove = new FluentButton(StockIcon.EDIT_REMOVE).withBordered(false).withButtonSize(FluentButtonSize.LARGE).withTooltip("Remove Plugins").withAction(this::removeSelected);
		reload = new FluentButton(StockIcon.ACTION_REFRESH_SYMBOLIC).withBordered(false).withButtonSize(FluentButtonSize.LARGE).withTooltip("Reload Plugins").withAction(this::reload);
		remove.setEnabled(false);
		
		browse = new FluentButton(StockIcon.DOCUMENT_OPEN_SYMBOLIC).withBordered(false).withButtonSize(FluentButtonSize.LARGE).withTooltip("Open Plugins Folder").withAction(this::browse);
		download = new FluentButton(StockIcon.GO_DOWN).withBordered(false).withButtonSize(FluentButtonSize.LARGE).withTooltip("Get More Plugins").withAction(this::download);
		
		ComponentStrip edits = new ComponentStrip(add, remove, reload);
		ComponentStrip tools = new ComponentStrip(browse, download);
		
		getHeader().setLeft(edits);
		getHeader().setRight(tools);
		getHeader().setCentre("Manage Plugins");
		
	}
	
	private void add() {
		StratusFilePanels.openFile(parent, "Import Plugins", Env.homeDirectory(), new SimpleFileExtension("Peakaboo Plugin", "jar"), result -> {
			if (!result.isPresent()) {
				return;
			}
			
			addPluginFile(result.get());

		});
	}
	

	private BoltPluginPrototype<? extends BoltPlugin> selectedPlugin() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if (node == null) {
			return null;
		}
		if (!(node.getUserObject() instanceof BoltPluginPrototype<?>)) {
			return null;
		}
		
		BoltPluginPrototype<? extends BoltPlugin> plugin = (BoltPluginPrototype<? extends BoltPlugin>) node.getUserObject();
		return plugin;
	}
	
	private void removeSelected() {	
		BoltPluginPrototype<? extends BoltPlugin> plugin = selectedPlugin();
		if (plugin != null) {
			remove(plugin);	
		}
	}
	
	private void remove(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		/*
		 * This is a little tricky. There's no rule that says that each plugin is in 
		 * it's own jar file. We need to confirm with the user that they want to 
		 * remove the jar file and all plugins that it contains.
		 */
		
		BoltContainer<? extends BoltPlugin> container = plugin.getContainer();
		if (!container.isDeletable()) {
			return;
		}
		
		if (container.isEmpty()) {
			return;
		}
		
		new LayerDialog(
				"Delete Plugin Container?", 
				"Are you sure you want to delete the container with the plugins:\n\n" + listToUL(container.getPlugins()), 
				StockIcon.BADGE_QUESTION)
			.addRight(
				new FluentButton("Delete").withAction(() -> {
					plugin.getContainer().delete();
					this.reload();
				}).withStateCritical()
				)
			.addLeft(new FluentButton("Cancel"))
			.showIn(parent);
		
		
	}
	
	private String listToUL(List<?> stuff) {
		StringBuilder buff = new StringBuilder();
		buff.append("<ul>");
		for (Object o : stuff) {
			String name = o.toString();
			if (o instanceof BoltPluginPrototype<?> plugin) {
				name = plugin.getName() + " (v" + plugin.getVersion() + ")";
			}
			buff.append("<li>" + name + "</li>");
		}
		buff.append("</ul>");
		return buff.toString();
	}
	
	

	/**
	 * Add a jar file containing plugins
	 */
	private void addPluginFile(File file) {
		
		boolean handled = false;
		
		try {
			handled |= addFileToManager(file, DataSourcePluginManager.system());
			handled |= addFileToManager(file, DataSinkPluginManager.system());
			handled |= addFileToManager(file, FilterPluginManager.system());
			handled |= addFileToManager(file, MapFilterPluginManager.system());
			
			for (BoltPluginManager<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
				handled |= addFileToManager(file, manager);
			}
			
		} catch (BoltImportException e) {
		
			PeakabooLog.get().log(Level.WARNING, e.getMessage(), e);
			new LayerDialog(
					"Import Failed", 
					"Peakboo was unable to import the plugin\n" + e.getMessage(), 
					StockIcon.BADGE_ERROR).showIn(parent);
			handled = true;
		}
		
		if (!handled) {
			new LayerDialog(
					"No Plugins Found", 
					"Peakboo could not fint any plugins in the file(s) provided", 
					StockIcon.BADGE_ERROR).showIn(parent);
		}
		
		reload();

	}
	
	/**
	 * Try adding a jar to a specific plugin manager. Return true if the given manager accepted the jar
	 */
	private boolean addFileToManager(File file, BoltPluginManager<? extends BoltPlugin> manager) throws BoltImportException {
		
		if (!manager.isImportable(file)) {
			return false;
		}
		
		BoltContainer<? extends BoltPlugin> container = manager.importOrUpgradeFile(file);
		
		this.reload();
		new LayerDialog(
				"Imported New Plugins", 
				"Peakboo successfully imported the following plugin(s):\n" + listToUL(container.getPlugins()), 
				StockIcon.BADGE_INFO).showIn(parent);

		return true;


	}
	
	
	public void reload() {
		DataSourcePluginManager.system().reload();
		DataSinkPluginManager.system().reload();
		FilterPluginManager.system().reload();
		MapFilterPluginManager.system().reload();
		
		for (BoltPluginManager<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
			manager.reload();
		}
		
		tree.setModel(buildTreeModel());
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
	
	private DefaultMutableTreeNode createPluginManagerRootNode(BoltPluginManager<?> manager) {
		DefaultMutableTreeNode sourcesNode = new DefaultMutableTreeNode(manager);
		for (BoltPluginPrototype<?> source :  manager.getPlugins()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sourcesNode.add(node);
		}
		for (BoltIssue<? extends BoltPlugin> issue : manager.getIssues()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(issue);
			sourcesNode.add(node);
		}
		return sourcesNode;
	}
	
	private TreeModel buildTreeModel() {
		
		DefaultMutableTreeNode plugins = new DefaultMutableTreeNode("Plugins");
		
		DefaultMutableTreeNode sourcesNode = createPluginManagerRootNode(DataSourcePluginManager.system());
		plugins.add(sourcesNode);
		
		DefaultMutableTreeNode sinksNode = createPluginManagerRootNode(DataSinkPluginManager.system());
		plugins.add(sinksNode);
		
		DefaultMutableTreeNode filtersNode = createPluginManagerRootNode(FilterPluginManager.system());
		plugins.add(filtersNode);
		
		DefaultMutableTreeNode mapFiltersNode = createPluginManagerRootNode(MapFilterPluginManager.system());
		plugins.add(mapFiltersNode);

		
		for (BoltPluginManager<? extends BoltPlugin> manager : Tier.provider().getPluginManagers()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(manager);
			plugins.add(node);
			
			for (BoltPluginPrototype<?> source :  manager.getPlugins()) {
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(source);
				node.add(subnode);
			}
			
		}
				
		
		return new DefaultTreeModel(plugins);
		
	}
	
	private JComponent pluginTree() {	
			
		tree = new JTree(buildTreeModel());
		tree.setCellRenderer(new StencilTreeCellRenderer<>(new PluginTreeWidget()));
		tree.setRootVisible(false);		
		
		tree.addTreeSelectionListener(tse -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			details.removeAll();
			if (node == null) { 
				details.add(new BlankMessagePanel("No Selection", noSelectionDescription), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else if (!node.isLeaf()) {
				
				BoltPluginManager<? extends BoltPlugin> manager = (BoltPluginManager<? extends BoltPlugin>) node.getUserObject();
				String interfaceDesc = manager.getInterfaceDescription();
				details.add(new BlankMessagePanel(manager.getInterfaceName(), interfaceDesc), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else {
				Object o = node.getUserObject();
				if (o instanceof BoltPluginPrototype<?>) {
					details.add(new PluginView((BoltPluginPrototype<? extends BoltPlugin>) o), BorderLayout.CENTER);
					remove.setEnabled(selectedPlugin().getContainer().isDeletable());
				} else if (o instanceof BoltIssue) {
					details.add(new IssueView((BoltIssue<? extends BoltPlugin>) o, this));
					remove.setEnabled(false);
				}
			}
			details.revalidate();
		});
		
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(250, 300));
		scroller.setBorder(new MatteBorder(0, 0, 0, 1, Stratus.getTheme().getWidgetBorder()));
		return scroller;
		
	}
	
}
