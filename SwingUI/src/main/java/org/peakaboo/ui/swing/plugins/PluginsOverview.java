package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

import org.peakaboo.calibration.CalibrationPluginManager;
import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.common.Env;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.plugin.DataSinkPlugin;
import org.peakaboo.datasink.plugin.DataSinkPluginManager;
import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.datasource.plugin.DataSourcePlugin;
import org.peakaboo.datasource.plugin.DataSourcePluginManager;
import org.peakaboo.datasource.plugin.JavaDataSourcePlugin;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.filter.plugins.FilterPlugin;
import org.peakaboo.filter.plugins.JavaFilterPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.eventful.EventfulEnumListener;
import org.peakaboo.framework.plural.monitor.SimpleTaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.stratus.StratusLookAndFeel;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.stratus.theme.LightTheme;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileExtension;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFilePanels;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog.MessageType;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.mapping.filter.model.MapFilterPluginManager;
import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.environment.DesktopApp;
import org.peakaboo.ui.swing.plotting.FileDrop;

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
		details.add(new PluginMessageView("No Selection", 0), BorderLayout.CENTER);
		body.add(details, BorderLayout.CENTER);
		new FileDrop(body, new FileDrop.Listener() {
			
			@Override
			public void urlsDropped(URL[] urls) {
				
				TaskMonitor<List<File>> monitor = FileDrop.getUrlsAsync(Arrays.asList(urls), optfiles -> {
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
		add = new ImageButton(StockIcon.EDIT_ADD).withButtonSize(ImageButtonSize.LARGE).withTooltip("Import Plugins").withAction(this::add);
		remove = new ImageButton(StockIcon.EDIT_REMOVE).withButtonSize(ImageButtonSize.LARGE).withTooltip("Remove Plugins").withAction(this::removeSelected);
		reload = new ImageButton(StockIcon.ACTION_REFRESH).withButtonSize(ImageButtonSize.LARGE).withTooltip("Reload Plugins").withAction(this::reload);
		remove.setEnabled(false);
		
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
				MessageType.QUESTION)
			.addRight(
				new ImageButton("Delete").withAction(() -> {
					plugin.getContainer().delete();
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
	
	

	/**
	 * Add a jar file containing plugins
	 */
	private void addPluginFile(File file) {
		
		boolean handled = false;
		
		try {
			handled |= addFileToManager(file, DataSourcePluginManager.SYSTEM);
			handled |= addFileToManager(file, DataSinkPluginManager.SYSTEM);
			handled |= addFileToManager(file, FilterPluginManager.SYSTEM);
			handled |= addFileToManager(file, MapFilterPluginManager.SYSTEM);
			if (Peakaboo.SHOW_QUANTITATIVE) handled |= addFileToManager(file, CalibrationPluginManager.SYSTEM);
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
		
		if (!manager.isImportable(file)) {
			return false;
		}
		
		BoltContainer<? extends BoltPlugin> container = manager.importOrUpgradeFile(file);
		
		this.reload();
		new LayerDialog(
				"Imported New Plugins", 
				"Peakboo successfully imported the following plugin(s):\n" + listToUL(container.getPlugins()), 
				MessageType.INFO).showIn(parent);

		return true;


	}
	
	
	public void reload() {
		DataSourcePluginManager.SYSTEM.reload();
		DataSinkPluginManager.SYSTEM.reload();
		FilterPluginManager.SYSTEM.reload();
		MapFilterPluginManager.SYSTEM.reload();
		if (Peakaboo.SHOW_QUANTITATIVE) CalibrationPluginManager.SYSTEM.reload();
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
		
		DefaultMutableTreeNode sourcesNode = createPluginManagerRootNode(DataSourcePluginManager.SYSTEM);
		plugins.add(sourcesNode);
		
		DefaultMutableTreeNode sinksNode = createPluginManagerRootNode(DataSinkPluginManager.SYSTEM);
		plugins.add(sinksNode);
		
		DefaultMutableTreeNode filtersNode = createPluginManagerRootNode(FilterPluginManager.SYSTEM);
		plugins.add(filtersNode);
		
		DefaultMutableTreeNode mapFiltersNode = createPluginManagerRootNode(MapFilterPluginManager.SYSTEM);
		plugins.add(mapFiltersNode);

		
		
		if (Peakaboo.SHOW_QUANTITATIVE) {
			DefaultMutableTreeNode calibrationsNode = new DefaultMutableTreeNode(CalibrationPluginManager.SYSTEM);
			plugins.add(calibrationsNode);
			for (BoltPluginPrototype<? extends CalibrationReference> source :  CalibrationPluginManager.SYSTEM.getPlugins()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
				calibrationsNode.add(node);
			}
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
			if (node == null) { 
				details.add(new PluginMessageView("No Selection", 0), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else if (!node.isLeaf()) {
				
				BoltPluginManager<? extends BoltPlugin> manager = (BoltPluginManager<? extends BoltPlugin>) node.getUserObject();
				String interfaceDesc = manager.getInterfaceDescription();
				details.add(new PluginMessageView(interfaceDesc, 300), BorderLayout.CENTER);
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
		scroller.setPreferredSize(new Dimension(200, 300));
		scroller.setBorder(new MatteBorder(0, 0, 0, 1, Swidget.dividerColor()));
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
