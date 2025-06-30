package org.peakaboo.ui.swing.plugins.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.FileDrop;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.stencil.StencilTreeCellRenderer;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.plugins.PluginsController;

public class PluginManager extends JPanel {

	private JTree tree;
	private JPanel details;
	
	private JButton add, remove, reload;
	

	private PluginsController controller;
	
	private static final String NO_SELECTION_DESCRIPTION = "Select a plugin or category from the sidebar to see information about available plugins.\n\nYou can add new plugins by dragging them into this window, or by using the 'Add' button in the toolbar";
	
	public PluginManager(PluginsController controller) {
		super(new BorderLayout());
		this.controller = controller;
		controller.addListener(() -> {
			// When the plugins controller emits an event, we refresh our view
			tree.setModel(buildTreeModel());
		});
		
		details = new JPanel(new BorderLayout());
		details.add(new BlankMessagePanel("No Selection", NO_SELECTION_DESCRIPTION), BorderLayout.CENTER);
		
		
		add = new FluentButton()
				.withIcon(StockIcon.EDIT_ADD, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Import Plugins")
				.withAction(controller::addFromFilesystem);
		
		remove = new FluentButton()
				.withIcon(StockIcon.EDIT_REMOVE, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Remove Plugins")
				.withAction(this::removeSelected);
		remove.setEnabled(false);
		
		reload = new FluentButton()
				.withIcon(StockIcon.ACTION_REFRESH_SYMBOLIC, Stratus.getTheme().getControlText())
				.withBordered(false)
				.withButtonSize(FluentButtonSize.LARGE)
				.withTooltip("Reload Plugins")
				.withAction(controller::reload);
		
		
		new FileDrop(this, new FileDrop.Listener() {
			// TODO move the impl to the controller
			@Override
			public void urlsDropped(URL[] urls) {
				
				TaskMonitor<List<File>> monitor = Peakaboo.getUrlsAsync(Arrays.asList(urls), optfiles -> {
					if (!optfiles.isPresent()) {
						return;
					}
					for (File file : optfiles.get()) {
						controller.install(file);
					}
				});
				
				TaskMonitorPanel.onLayerPanel(monitor, controller.getParentLayer());

			}
			
			@Override
			public void filesDropped(File[] files) {
				for (File file : files) {
					controller.install(file);
				}
			}
		});
		
		this.add(pluginTree(), BorderLayout.WEST);
		this.add(details, BorderLayout.CENTER);

		
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
		controller.remove(selectedPlugin());
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
				details.add(new BlankMessagePanel("No Selection", NO_SELECTION_DESCRIPTION), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else if (!node.isLeaf()) {
				
				BoltPluginRegistry<? extends BoltPlugin> manager = (BoltPluginRegistry) node.getUserObject();
				String interfaceDesc = manager.getInterfaceDescription();
				details.add(new BlankMessagePanel(manager.getInterfaceName(), interfaceDesc), BorderLayout.CENTER);
				remove.setEnabled(false);
			} else {
				Object o = node.getUserObject();
				if (o instanceof PluginDescriptor<?>) {
					details.add(new PluginView((PluginDescriptor<BoltPlugin>) o), BorderLayout.CENTER);
					remove.setEnabled(selectedPlugin().getContainer().isDeletable());
				} else if (o instanceof BoltIssue) {
					details.add(new IssueView((BoltIssue<BoltPlugin>) o, controller));
					remove.setEnabled(false);
				}
			}
			details.revalidate();
		});
		
		
		JScrollPane scroller = new JScrollPane(tree);
		scroller.setPreferredSize(new Dimension(250, 300));
		scroller.setBorder(Spacing.bNone());
				
		// Now build the toolbar at the bottom of the tree. We'll use a buttonbox
		ComponentStrip tools = new ComponentStrip(add, remove, reload);
		tools.setBorder(Spacing.bSmall());
		
		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.setBorder(new MatteBorder(0, 0, 0, 1, Stratus.getTheme().getWidgetBorder()));
		sidebar.add(scroller, BorderLayout.CENTER);
		sidebar.add(tools, BorderLayout.SOUTH);
		
		
		return sidebar;
		
	}
	
}
