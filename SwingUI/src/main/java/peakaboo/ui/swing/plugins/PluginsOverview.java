package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import commonenvironment.Apps;
import commonenvironment.Env;
import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.common.PluginManager;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.datasource.plugin.JavaDataSourcePlugin;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.ui.swing.plotting.FileDrop;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ClearPanel;
import swidget.widgets.HeaderBox;
import swidget.widgets.HeaderBoxPanel;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterfaceDialog;
import swidget.widgets.tabbedinterface.TabbedInterfacePanel;

public class PluginsOverview extends JPanel {

	JPanel details;
	JTree tree;
	TabbedInterfacePanel parent;
	
	JButton close, add, remove, reload, browse, download;
	
	public PluginsOverview(TabbedInterfacePanel parent) {
		super(new BorderLayout());
		
		this.parent = parent;
		
		JPanel body = new JPanel(new BorderLayout());
		setPreferredSize(new Dimension(800, 350));
		body.add(pluginTree(), BorderLayout.WEST);
		details = new JPanel(new BorderLayout());
		body.add(details, BorderLayout.CENTER);
				
		close = HeaderBox.button("Close", () -> parent.popModalComponent());
		
		add = HeaderBox.button(StockIcon.EDIT_ADD, "Import Plugins", this::add);
		remove = HeaderBox.button(StockIcon.EDIT_REMOVE, "Remove Plugins", this::removeSelected);
		
		reload = HeaderBox.button(StockIcon.ACTION_REFRESH, "Reload Plugins", this::reload);
		browse = HeaderBox.button(StockIcon.PLACE_FOLDER_OPEN, "Open Plugins Folder", this::browse);
		download = HeaderBox.button(StockIcon.GO_DOWN, "Get More Plugins", this::download);
		
		ButtonBox left = new ButtonBox(Spacing.bNone(), Spacing.medium, false);
		left.setOpaque(false);
		left.addLeft(add);
		left.addLeft(remove);
		left.addLeft(reload);
		left.addLeft(new ClearPanel()); //spacing
		left.addLeft(browse);
		left.addLeft(download);
		
		HeaderBoxPanel main = new HeaderBoxPanel(new HeaderBox(left, "Manage Plugins", close), body);
		
		this.add(main, BorderLayout.CENTER);
		
		new FileDrop(this, files -> {
			for (File file : files) {
				addJar(file);
			}
		});

		
	}
	
	private void add() {
		SwidgetFilePanels.openFile(parent, "Import Plugins", Env.homeDirectory(), new SimpleFileExtension("Peakaboo Plugin", "jar"), result -> {
			if (!result.isPresent()) {
				return;
			}
			
			addJar(result.get());
			
		});
	}
	
	private boolean isRemovable(BoltPluginController<? extends BoltPlugin> plugin) {
		return plugin.getSource() != null;
	}
	
	private BoltPluginController<? extends BoltPlugin> selectedPlugin() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if (!(node.getUserObject() instanceof BoltPluginController<?>)) {
			return null;
		}
		
		BoltPluginController<? extends BoltPlugin> plugin = (BoltPluginController<? extends BoltPlugin>) node.getUserObject();
		return plugin;
	}
	
	private void removeSelected() {	
		BoltPluginController<? extends BoltPlugin> plugin = selectedPlugin();
		if (plugin != null) {
			remove(plugin);	
		}
	}
	
	private void remove(BoltPluginController<? extends BoltPlugin> plugin) {
		/*
		 * This is a little tricky. There's no rule that says that each plugin is in 
		 * it's own jar file. We need to confirm with the user that they want to 
		 * remove the jar file and all plugins that it contains.
		 */
		
		PluginManager<? extends BoltPlugin> manager = managerForPlugin(plugin);
		if (manager == null) {
			return;
		}
		
		if (!isRemovable(plugin)) {
			return;
		}
		
		File jar;
		BoltPluginSet<? extends BoltPlugin> set;
		try {
			jar = new File(plugin.getSource().toURI());
			set = manager.pluginsInJar(jar);
		} catch (URISyntaxException e) {
			PeakabooLog.get().log(Level.WARNING, "Cannot lookup jar for plugin", e);
			return;
		}
		
		if (set.getAll().size() == 0) {
			return;
		}
		
		new TabbedInterfaceDialog(
				"Delete Plugin Archive?", 
				"Are you sure you want to delete the archive containing the plugins:\n\n" +
						set.getAll().stream().map(p -> p.toString()).reduce((a, b) -> a + "\n" + b).get(), 
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION,
				v -> {
					if (v != (Integer)JOptionPane.YES_OPTION) {
						return;
					}
					manager.removeJar(jar);
					this.reload();
				}
			).showIn(parent);
		
		
	}
	
	private PluginManager<? extends BoltPlugin> managerForPlugin(BoltPluginController<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == JavaDataSourcePlugin.class) {
			return DataSourcePluginManager.SYSTEM;
		}
		
		if (pluginBaseClass == JavaDataSinkPlugin.class) {
			return DataSinkPluginManager.SYSTEM;
		}
		
		if (pluginBaseClass == JavaFilterPlugin.class) {
			return FilterPluginManager.SYSTEM;
		}
		
		return null;
		
	}
	
	private void addJar(File jar) {
		
		boolean added = false;
		
		added |= DataSourcePluginManager.SYSTEM.importJar(jar);
		added |= DataSinkPluginManager.SYSTEM.importJar(jar);
		added |= FilterPluginManager.SYSTEM.importJar(jar);
		
		if (added) {
			this.reload();
		}
	}
	
	
	private void reload() {
		DataSourcePluginManager.SYSTEM.reload();
		DataSinkPluginManager.SYSTEM.reload();
		FilterPluginManager.SYSTEM.reload();
		tree.setModel(buildTreeModel());
	}
	
	private void browse() {
		File appDataDir = Configuration.appDir("Plugins");
		appDataDir.mkdirs();
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(appDataDir);
		} catch (IOException e1) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to open plugin folder", e1);
		}
	}
	
	private void download() {
		Apps.browser("https://github.com/nsherry4/PeakabooPlugins");
	}
	
	private TreeModel buildTreeModel() {
		
		DefaultMutableTreeNode plugins = new DefaultMutableTreeNode("Plugins");
		
		DefaultMutableTreeNode sourcesNode = new DefaultMutableTreeNode("Data Sources");
		plugins.add(sourcesNode);
		for (BoltPluginController<? extends DataSourcePlugin> source :  DataSourcePluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sourcesNode.add(node);
		}
		
		DefaultMutableTreeNode sinksNode = new DefaultMutableTreeNode("Data Sinks");
		plugins.add(sinksNode);
		for (BoltPluginController<? extends DataSinkPlugin> source :  DataSinkPluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sinksNode.add(node);
		}
		
		DefaultMutableTreeNode filtersNode = new DefaultMutableTreeNode("Filters");
		plugins.add(filtersNode);
		for (BoltPluginController<? extends FilterPlugin> source :  FilterPluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			filtersNode.add(node);
		}
		
		
		return new DefaultTreeModel(plugins);
		
	}
	
	private JComponent pluginTree() {	
			
		tree = new JTree(buildTreeModel());

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);
		
		tree.setRootVisible(false);
		
		tree.addTreeSelectionListener(tse -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			details.removeAll();
			if (node == null || !node.isLeaf()) { 
				details.add(new JPanel(), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else {
				details.add(new PluginView((BoltPluginController<? extends BoltPlugin>) node.getUserObject()), BorderLayout.CENTER);
				remove.setEnabled(isRemovable(selectedPlugin()));
			}
			details.revalidate();
		});
		
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(200, 300));
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scroller;
		
	}
	
}
