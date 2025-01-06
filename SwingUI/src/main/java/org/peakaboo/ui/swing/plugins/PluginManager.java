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
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusText;
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
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.app.widgets.FileDrop;

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
		new FileDrop(body, (File[] files) -> {
			for (File file : files) {
				addPluginFile(file);
			}}
		);

		setBody(body);
		
		
		//header controls
		add = new FluentButton()
				.withIcon(StockIcon.EDIT_ADD, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Import Plugins")
				.withAction(this::add);
		
		remove = new FluentButton()
				.withIcon(StockIcon.EDIT_REMOVE, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Remove Plugins")
				.withAction(this::removeSelected);
		
		reload = new FluentButton()
				.withIcon(StockIcon.ACTION_REFRESH_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Reload Plugins")
				.withAction(this::reload);
		
		remove.setEnabled(false);
		
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
	

	private PluginDescriptor<BoltPlugin> selectedPlugin() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if (node == null) {
			return null;
		}
		if (!(node.getUserObject() instanceof PluginDescriptor<?>)) {
			return null;
		}
		
		PluginDescriptor<BoltPlugin> plugin = (PluginDescriptor<BoltPlugin>) node.getUserObject();
		return plugin;
	}
	
	private void removeSelected() {	
		remove(selectedPlugin());
	}
	
	private void remove(PluginDescriptor<BoltPlugin> plugin) {
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
		
		new LayerDialog("Delete Plugin Bundle?", descriptorsToHTML(container.getPlugins()))
			.addRight(
				new FluentButton("Delete").withAction(() -> {
					plugin.getContainer().delete();
					this.reload();
				}).withStateCritical()
				)
			.addLeft(new FluentButton("Cancel"))
			.showIn(parent);
		
		
	}
	
	private String descriptorsToHTML(List<?> stuff) {
		
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
	
	private String descriptorToHTML(PluginDescriptor<?> plugin) {
		// The details panel is just a convenient component which hasn't had its default font properties changes
		String wrappedDescription = StratusText.lineWrapHTMLInline(details, plugin.getDescription(), 400);
		return String.format("<div style='padding: 5px;'><div style='font-size: 20pt;'>%s</div><div style='font-size: 10pt; padding-bottom: 5px;'>version %s</div><div style=''>%s</div></div>", plugin.getName(), plugin.getVersion(), wrappedDescription);
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
	
	/**
	 * Add a jar file containing plugins
	 */
	private void addPluginFile(File file) {
		
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
	private boolean addFileToManager(File file, BoltPluginRegistry<? extends BoltPlugin> manager) throws BoltImportException {
		
		if (!manager.isImportable(file)) {
			return false;
		}
		
		BoltContainer<? extends BoltPlugin> container = manager.importOrUpgradeFile(file);
		
		this.reload();
		new LayerDialog("Imported New Plugins", descriptorsToHTML(container.getPlugins())).showIn(parent);

		return true;


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
	
	private DefaultMutableTreeNode createPluginManagerRootNode(PluginRegistry<? extends BoltPlugin> manager) {
		DefaultMutableTreeNode sourcesNode = new DefaultMutableTreeNode(manager);
		for (PluginDescriptor<?> source :  manager.getPlugins()) {
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
		
		DefaultMutableTreeNode sourcesNode = createPluginManagerRootNode(DataSourceRegistry.system());
		plugins.add(sourcesNode);
		
		DefaultMutableTreeNode sinksNode = createPluginManagerRootNode(DataSinkRegistry.system());
		plugins.add(sinksNode);
		
		DefaultMutableTreeNode filtersNode = createPluginManagerRootNode(FilterRegistry.system());
		plugins.add(filtersNode);
		
		DefaultMutableTreeNode mapFiltersNode = createPluginManagerRootNode(MapFilterRegistry.system());
		plugins.add(mapFiltersNode);

				
		for (var manager : Tier.provider().getPluginManagers()) {
			DefaultMutableTreeNode customNode = createPluginManagerRootNode(manager);
			plugins.add(customNode);		
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
				
				BoltPluginRegistry<? extends BoltPlugin> manager = (BoltPluginRegistry<? extends BoltPlugin>) node.getUserObject();
				String interfaceDesc = manager.getInterfaceDescription();
				details.add(new BlankMessagePanel(manager.getInterfaceName(), interfaceDesc), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else {
				Object o = node.getUserObject();
				if (o instanceof PluginDescriptor<?>) {
					details.add(new PluginView((PluginDescriptor<BoltPlugin>) o), BorderLayout.CENTER);
					remove.setEnabled(selectedPlugin().getContainer().isDeletable());
				} else if (o instanceof BoltIssue) {
					details.add(new IssueView((BoltIssue<BoltPlugin>) o, this));
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
