package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.datasource.plugin.JavaDataSourcePlugin;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.ui.swing.plotting.FileDrop;
import stratus.StratusLookAndFeel;
import stratus.theme.DarkTheme;
import stratus.theme.LightTheme;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ClearPanel;
import swidget.widgets.HeaderBox;
import swidget.widgets.HeaderBoxPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.ButtonSize;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.LayerDialogs;
import swidget.widgets.layerpanel.LayerDialogs.MessageType;
import swidget.widgets.Spacing;

public class PluginsOverview extends JPanel {

	JPanel details;
	JTree tree;
	LayerPanel parent;
	
	JButton close, add, remove, reload, browse, download;
	
	public PluginsOverview(LayerPanel parent) {
		super(new BorderLayout());
		
		this.parent = parent;
		
		JPanel body = new JPanel(new BorderLayout());
		setPreferredSize(new Dimension(800, 350));
		body.add(pluginTree(), BorderLayout.WEST);
		details = new JPanel(new BorderLayout());
		body.add(details, BorderLayout.CENTER);
				
		close = new ImageButton("Close").withAction(() -> parent.popLayer());
		
		add = new ImageButton(StockIcon.EDIT_ADD).withButtonSize(ButtonSize.LARGE).withBordered(false).withTooltip("Import Plugins").withAction(this::add);
		remove = new ImageButton(StockIcon.EDIT_REMOVE).withButtonSize(ButtonSize.LARGE).withBordered(false).withTooltip("Remove Plugins").withAction(this::removeSelected);
		
		reload = new ImageButton(StockIcon.ACTION_REFRESH).withButtonSize(ButtonSize.LARGE).withBordered(false).withTooltip("Reload Plugins").withAction(this::reload);
		browse = new ImageButton(StockIcon.PLACE_FOLDER_OPEN).withButtonSize(ButtonSize.LARGE).withBordered(false).withTooltip("Open Plugins Folder").withAction(this::browse);
		download = new ImageButton(StockIcon.GO_DOWN).withButtonSize(ButtonSize.LARGE).withBordered(false).withTooltip("Get More Plugins").withAction(this::download);
		
		ButtonBox left = new ButtonBox(Spacing.tiny, false);
		left.setOpaque(false);
		left.addLeft(add);
		left.addLeft(remove);
		left.addLeft(reload);
		left.addLeft(new ClearPanel()); //spacing
		left.addLeft(browse);
		left.addLeft(download);
		
		HeaderBoxPanel main = new HeaderBoxPanel(new HeaderBox(left, "Manage Plugins", close), body);
		
		this.add(main, BorderLayout.CENTER);
		
		new FileDrop(body, files -> {
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
		
		if (node == null) {
			return null;
		}
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
		
		BoltPluginManager<? extends BoltPlugin> manager = managerForPlugin(plugin);
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
		
		new LayerDialogs(
				"Delete Plugin Archive?", 
				"Are you sure you want to delete the archive containing the plugins:\n\n" + listToUL(set.getAll()), 
				MessageType.QUESTION)
			.addRight(
				new ImageButton("Yes").withAction(() -> {
					manager.removeJar(jar);
					this.reload();
				}))
			.addLeft(new ImageButton("No"))
			.showIn(parent);
		
		
	}
	
	private String listToUL(List<?> stuff) {
		StringBuffer buff = new StringBuffer();
		buff.append("<ul>");
		for (Object o : stuff) {
			String name = o.toString();
			if (o instanceof BoltPluginController<?>) {
				BoltPluginController<?> plugin = (BoltPluginController<?>) o;
				name = plugin.getName() + " (v" + plugin.getVersion() + ")";
			}
			buff.append("<li>" + name + "</li>");
		}
		buff.append("</ul>");
		return buff.toString();
	}
	
	private BoltPluginManager<? extends BoltPlugin> managerForPlugin(BoltPluginController<? extends BoltPlugin> plugin) {
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
		
		try {
			added |= addJarToManager(jar, DataSourcePluginManager.SYSTEM);
			added |= addJarToManager(jar, DataSinkPluginManager.SYSTEM);
			added |= addJarToManager(jar, FilterPluginManager.SYSTEM);		
		} catch (BoltImportException e) {
		
			PeakabooLog.get().log(Level.WARNING, e.getMessage(), e);
			new LayerDialogs(
					"Import Failed", 
					"Peakboo was unable to import the plugin\n" + e.getMessage(), 
					MessageType.ERROR).showIn(parent);
			added = true;
		}
		
		if (!added) {
			new LayerDialogs(
					"No Plugins Found", 
					"Peakboo could not fint any plugins in the file(s) provided", 
					MessageType.ERROR).showIn(parent);
		}
		
		reload();
		
		

	}
	
	private boolean addJarToManager(File jar, BoltPluginManager<? extends BoltPlugin> manager) throws BoltImportException {
		
		if (!manager.jarContainsPlugins(jar)) {
			return false;
		}
		
		Optional<File> upgradeTarget = manager.jarUpgradeTarget(jar);
		//looks like this jar is an upgrade for an existing jar
		if (upgradeTarget.isPresent()) {
			manager.removeJar(upgradeTarget.get());
		}
		
		BoltPluginSet<? extends BoltPlugin> plugins = manager.importJar(jar);
		
		this.reload();
		new LayerDialogs(
				"Imported New Plugins", 
				"Peakboo successfully imported the following plugin(s):\n" + listToUL(plugins.getAll()), 
				MessageType.INFO).showIn(parent);

		return true;


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
		renderer.setBorder(Spacing.bSmall());
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
	
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(new StratusLookAndFeel(new LightTheme()));
		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(640, 480));
		
		ImageButton b1 = new ImageButton("OK").withButtonSize(ButtonSize.LARGE);
		ImageButton b2 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ButtonSize.LARGE);

		ImageButton b3 = new ImageButton("OK").withButtonSize(ButtonSize.LARGE);
		ImageButton b4 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ButtonSize.LARGE);
		
		ImageButton b5 = new ImageButton("OK").withButtonSize(ButtonSize.LARGE);
		ImageButton b6 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ButtonSize.LARGE);
		
		ButtonBox box = new ButtonBox();
		
		box.addLeft(b1);
		box.addLeft(b2);
		
		box.addCentre(b3);
		box.addCentre(b4);
		
		box.addRight(b5);
		box.addRight(b6);
		
		
		System.out.println(frame.getContentPane().getLayout());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(box, BorderLayout.SOUTH);
		
		frame.pack();
		
		frame.setVisible(true);
				
		
	}
	
}
