package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;
import peakaboo.calibration.CalibrationPluginManager;
import peakaboo.calibration.CalibrationReference;
import peakaboo.common.Env;
import peakaboo.common.PeakabooLog;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.datasource.plugin.JavaDataSourcePlugin;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.ui.swing.environment.DesktopApp;
import peakaboo.ui.swing.plotting.FileDrop;
import stratus.StratusLookAndFeel;
import stratus.controls.ButtonLinker;
import stratus.theme.LightTheme;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonSize;
import swidget.widgets.layerpanel.HeaderLayer;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layout.ButtonBox;

public class PluginsOverview extends HeaderLayer {

	JPanel details;
	JTree tree;
	LayerPanel parent;
	
	JButton close, add, remove, reload, browse, download;
	
	public PluginsOverview(LayerPanel parent) {
		super(parent, true);
		this.parent = parent;
		getContentRoot().setPreferredSize(new Dimension(800, 350));
		
		//body
		JPanel body = new JPanel(new BorderLayout());
		body.add(pluginTree(), BorderLayout.WEST);
		details = new JPanel(new BorderLayout());
		body.add(details, BorderLayout.CENTER);
		new FileDrop(body, files -> {
			for (File file : files) {
				addPluginFile(file);
			}
		});
		setBody(body);
		
		
		//header controls
		add = new ImageButton(StockIcon.EDIT_ADD).withButtonSize(ImageButtonSize.LARGE).withTooltip("Import Plugins").withAction(this::add);
		remove = new ImageButton(StockIcon.EDIT_REMOVE).withButtonSize(ImageButtonSize.LARGE).withTooltip("Remove Plugins").withAction(this::removeSelected);
		reload = new ImageButton(StockIcon.ACTION_REFRESH).withButtonSize(ImageButtonSize.LARGE).withTooltip("Reload Plugins").withAction(this::reload);
		
		browse = new ImageButton(StockIcon.PLACE_FOLDER_OPEN).withButtonSize(ImageButtonSize.LARGE).withTooltip("Open Plugins Folder").withAction(this::browse);
		download = new ImageButton(StockIcon.GO_DOWN).withButtonSize(ImageButtonSize.LARGE).withTooltip("Get More Plugins").withAction(this::download);
		
		ButtonLinker edits = new ButtonLinker(add, remove, reload);
		ButtonLinker tools = new ButtonLinker(browse, download);
		
		getHeader().setLeft(edits);
		getHeader().setRight(tools);
		getHeader().setCentre("Manage Plugins");
		
	}
	
	private void add() {
		SwidgetFilePanels.openFile(parent, "Import Plugins", Env.homeDirectory(), new SimpleFileExtension("Peakaboo Plugin", "jar"), result -> {
			if (!result.isPresent()) {
				return;
			}
			
			addPluginFile(result.get());

		});
	}
	
	private boolean isRemovable(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		return plugin.getSource() != null;
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
		
		BoltPluginManager<? extends BoltPlugin> manager = managerForPlugin(plugin);
		if (manager == null) {
			return;
		}
		
		if (!isRemovable(plugin)) {
			return;
		}
		
		File file;
		BoltPluginSet<? extends BoltPlugin> set;
		try {
			file = new File(plugin.getSource().toURI());
			set = manager.pluginsInFile(file);
		} catch (URISyntaxException e) {
			PeakabooLog.get().log(Level.WARNING, "Cannot lookup jar for plugin", e);
			return;
		}
		
		if (set.getAll().size() == 0) {
			return;
		}
		
		new LayerDialog(
				"Delete Plugin Archive?", 
				"Are you sure you want to delete the archive containing the plugins:\n\n" + listToUL(set.getAll()), 
				MessageType.QUESTION)
			.addRight(
				new ImageButton("Delete").withAction(() -> {
					manager.removeFile(file);
					this.reload();
				}).withStateCritical()
				)
			.addLeft(new ImageButton("Cancel"))
			.showIn(parent);
		
		
	}
	
	private String listToUL(List<?> stuff) {
		StringBuffer buff = new StringBuffer();
		buff.append("<ul>");
		for (Object o : stuff) {
			String name = o.toString();
			if (o instanceof BoltPluginPrototype<?>) {
				BoltPluginPrototype<?> plugin = (BoltPluginPrototype<?>) o;
				name = plugin.getName() + " (v" + plugin.getVersion() + ")";
			}
			buff.append("<li>" + name + "</li>");
		}
		buff.append("</ul>");
		return buff.toString();
	}
	
	private BoltPluginManager<? extends BoltPlugin> managerForPlugin(BoltPluginPrototype<? extends BoltPlugin> plugin) {
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
		
		if (pluginBaseClass == CalibrationReference.class) {
			return CalibrationPluginManager.SYSTEM;
		}
		
		return null;
		
	}
	

	/**
	 * Add a jar file containing plugins
	 */
	private void addPluginFile(File file) {
		
		boolean handled = false;
		
		try {
			handled |= addFileToManager(file, DataSourcePluginManager.SYSTEM);
			handled |= addFileToManager(file, DataSinkPluginManager.SYSTEM);
			handled |= addFileToManager(file, FilterPluginManager.SYSTEM);
			handled |= addFileToManager(file, CalibrationPluginManager.SYSTEM);
		} catch (BoltImportException e) {
		
			PeakabooLog.get().log(Level.WARNING, e.getMessage(), e);
			new LayerDialog(
					"Import Failed", 
					"Peakboo was unable to import the plugin\n" + e.getMessage(), 
					MessageType.ERROR).showIn(parent);
			handled = true;
		}
		
		if (!handled) {
			new LayerDialog(
					"No Plugins Found", 
					"Peakboo could not fint any plugins in the file(s) provided", 
					MessageType.ERROR).showIn(parent);
		}
		
		reload();

	}
	
	/**
	 * Try adding a jar to a specific plugin manager. Return true if the given manager accepted the jar
	 */
	private boolean addFileToManager(File file, BoltPluginManager<? extends BoltPlugin> manager) throws BoltImportException {
		
		if (!manager.fileContainsPlugins(file)) {
			return false;
		}

		BoltPluginSet<? extends BoltPlugin> plugins = manager.importOrUpgradeFile(file);
		
		this.reload();
		new LayerDialog(
				"Imported New Plugins", 
				"Peakboo successfully imported the following plugin(s):\n" + listToUL(plugins.getAll()), 
				MessageType.INFO).showIn(parent);

		return true;


	}
	
	
	private void reload() {
		DataSourcePluginManager.SYSTEM.reload();
		DataSinkPluginManager.SYSTEM.reload();
		FilterPluginManager.SYSTEM.reload();
		CalibrationPluginManager.SYSTEM.reload();
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
	
	private TreeModel buildTreeModel() {
		
		DefaultMutableTreeNode plugins = new DefaultMutableTreeNode("Plugins");
		
		DefaultMutableTreeNode sourcesNode = new DefaultMutableTreeNode("Data Sources");
		plugins.add(sourcesNode);
		for (BoltPluginPrototype<? extends DataSourcePlugin> source :  DataSourcePluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sourcesNode.add(node);
		}
		
		DefaultMutableTreeNode sinksNode = new DefaultMutableTreeNode("Data Sinks");
		plugins.add(sinksNode);
		for (BoltPluginPrototype<? extends DataSinkPlugin> source :  DataSinkPluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sinksNode.add(node);
		}
		
		DefaultMutableTreeNode filtersNode = new DefaultMutableTreeNode("Filters");
		plugins.add(filtersNode);
		for (BoltPluginPrototype<? extends FilterPlugin> source :  FilterPluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			filtersNode.add(node);
		}
		
		DefaultMutableTreeNode calibrationsNode = new DefaultMutableTreeNode("Calibration References");
		plugins.add(calibrationsNode);
		for (BoltPluginPrototype<? extends CalibrationReference> source :  CalibrationPluginManager.SYSTEM.getPlugins().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			calibrationsNode.add(node);
		}
		
		
		return new DefaultTreeModel(plugins);
		
	}
	
	private JComponent pluginTree() {	
			
		tree = new JTree(buildTreeModel());

		DefaultTreeCellRenderer renderer = new PluginTreeRenderer();
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
				details.add(new PluginView((BoltPluginPrototype<? extends BoltPlugin>) node.getUserObject()), BorderLayout.CENTER);
				remove.setEnabled(isRemovable(selectedPlugin()));
			}
			details.revalidate();
		});
		
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(200, 300));
		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		scroller.setBorder(new MatteBorder(0, 0, 0, 1, dividerColour));
		return scroller;
		
	}
	
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(new StratusLookAndFeel(new LightTheme()));
		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(640, 480));
		
		ImageButton b1 = new ImageButton("OK").withButtonSize(ImageButtonSize.LARGE);
		ImageButton b2 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ImageButtonSize.LARGE);

		ImageButton b3 = new ImageButton("OK").withButtonSize(ImageButtonSize.LARGE);
		ImageButton b4 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ImageButtonSize.LARGE);
		
		ImageButton b5 = new ImageButton("OK").withButtonSize(ImageButtonSize.LARGE);
		ImageButton b6 = new ImageButton("OK", StockIcon.CHOOSE_OK).withButtonSize(ImageButtonSize.LARGE);
		
		ButtonBox box = new ButtonBox();
		
		box.addLeft(b1);
		box.addLeft(b2);
		
		box.addCentre(b3);
		box.addCentre(b4);
		
		box.addRight(b5);
		box.addRight(b6);
		
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(box, BorderLayout.SOUTH);
		
		frame.pack();
		
		frame.setVisible(true);
				
		
	}
	
}
