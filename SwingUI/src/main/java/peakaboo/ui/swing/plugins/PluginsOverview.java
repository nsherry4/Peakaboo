package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import peakaboo.datasink.plugin.DataSinkLoader;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasource.plugin.DataSourceLoader;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.filter.model.FilterLoader;
import peakaboo.filter.plugins.FilterPlugin;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;

public class PluginsOverview extends JPanel {

	JPanel details;
	
	public PluginsOverview() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(800, 300));
		
		add(pluginTree(), BorderLayout.WEST);
		
		details = new JPanel(new BorderLayout());
		add(details, BorderLayout.CENTER);
		
	}
	
		
	private JComponent pluginTree() {
		DefaultMutableTreeNode plugins = new DefaultMutableTreeNode("Plugins");
				
		DefaultMutableTreeNode sourcesNode = new DefaultMutableTreeNode("Data Sources");
		plugins.add(sourcesNode);
		for (BoltPluginController<? extends DataSourcePlugin> source :  DataSourceLoader.getPluginSet().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sourcesNode.add(node);
		}
		
		DefaultMutableTreeNode sinksNode = new DefaultMutableTreeNode("Data Sinks");
		plugins.add(sinksNode);
		for (BoltPluginController<? extends DataSinkPlugin> source :  DataSinkLoader.getPluginSet().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			sinksNode.add(node);
		}
		
		DefaultMutableTreeNode filtersNode = new DefaultMutableTreeNode("Filters");
		plugins.add(filtersNode);
		for (BoltPluginController<? extends FilterPlugin> source :  FilterLoader.getPluginSet().getAll()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(source);
			filtersNode.add(node);
		}
		
			
		
		JTree tree = new JTree(plugins);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);
		
		tree.setRootVisible(false);
		
		tree.addTreeSelectionListener(tse -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			details.removeAll();
			if (!node.isLeaf()) { return; }
			details.add(new PluginView((BoltPluginController<? extends BoltPlugin>) node.getUserObject()), BorderLayout.CENTER);
		});
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(200, 300));
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scroller;
		
	}
	
}
