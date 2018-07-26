package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import commonenvironment.Apps;
import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.common.Configuration;
import peakaboo.common.PeakabooLog;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.filter.plugins.FilterPlugin;
import peakaboo.ui.swing.plotting.FileDrop;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.HeaderBox;
import swidget.widgets.HeaderBoxPanel;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterfacePanel;

public class PluginsOverview extends JPanel {

	JPanel details;
	JTree tree;
	
	public PluginsOverview(TabbedInterfacePanel parent) {
		super(new BorderLayout());
		
		
		JPanel body = new JPanel(new BorderLayout());
		setPreferredSize(new Dimension(800, 350));
		body.add(pluginTree(), BorderLayout.WEST);
		details = new JPanel(new BorderLayout());
		body.add(details, BorderLayout.CENTER);
				
		JButton close = HeaderBox.button("Close", () -> parent.popModalComponent());
		
		JButton reload = HeaderBox.button(StockIcon.ACTION_REFRESH, "Reload Plugins", this::reload);
		JButton browse = HeaderBox.button(StockIcon.PLACE_FOLDER_OPEN, "Open Plugins Folder", this::browse);
		JButton download = HeaderBox.button(StockIcon.GO_DOWN, "Get More Plugins", this::download);
		
		ButtonBox left = new ButtonBox(Spacing.bNone(), Spacing.medium, false);
		left.setOpaque(false);
		left.addLeft(reload);
		left.addLeft(browse);
		left.addLeft(download);
		
		HeaderBoxPanel main = new HeaderBoxPanel(new HeaderBox(left, "Manage Plugins", close), body);
		
		this.add(main, BorderLayout.CENTER);
		
		new FileDrop(this, files -> {
			for (File file : files) {
				addPlugin(file);
			}
		});

		
	}
		
	
	private void addPlugin(File jar) {
		System.out.println(jar);
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
			} else {
				details.add(new PluginView((BoltPluginController<? extends BoltPlugin>) node.getUserObject()), BorderLayout.CENTER);
			}
			details.revalidate();
		});
		
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(200, 300));
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scroller;
		
	}
	
}
