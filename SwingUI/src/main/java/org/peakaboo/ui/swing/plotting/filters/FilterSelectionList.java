package org.peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.filter.plugins.FilterPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.listcontrols.SelectionListControls;


class FilterSelectionList extends ClearPanel
{

	private FilteringController		controller;
	private FiltersetViewer			owner;

	private JTree					tree;
	
	
	FilterSelectionList(FilteringController filteringController, FiltersetViewer ownerUI)
	{

		this.controller = filteringController;
		this.owner = ownerUI;
		
		
		this.setLayout(new BorderLayout());

		JScrollPane scroller = new JScrollPane(createFilterTree(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(Spacing.bNone());
		
		this.add(scroller, BorderLayout.CENTER);
		add(createControls(), BorderLayout.NORTH);

	}


	private JTree createFilterTree()
	{

		TreeModel m = new FilterSelectionTreeModel();

		tree = new JTree(m);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		
		FilterSelectionRenderer renderer = new FilterSelectionRenderer();
		renderer.setLeafIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		ToolTipManager.sharedInstance().registerComponent(tree);
		
		return tree;
	}


	private JPanel createControls() {

		SelectionListControls controls = new SelectionListControls("Filter", "Add Filter") {
		
			@Override
			protected void cancel()
			{
				owner.showEditPane();
			}
		
		
			@Override
			protected void approve()
			{
				TreePath path = tree.getSelectionPath();
				if (path == null) {owner.showEditPane(); return;}
				
				Object leaf = path.getLastPathComponent();

				if (leaf instanceof BoltPluginPrototype<?>) {
					@SuppressWarnings("unchecked")
					BoltPluginPrototype<? extends FilterPlugin> plugin = (BoltPluginPrototype<? extends FilterPlugin>) leaf;
					Filter filter = plugin.create();
					filter.initialize();
					controller.addFilter(filter);
				}

				owner.showEditPane();
			}
		};
		controls.setOpaque(false);
		
		return controls;

	}

}

class FilterSelectionTreeModel implements TreeModel {

	public void valueForPathChanged(TreePath path, Object newValue) {
		// NOOP
	}

	public void removeTreeModelListener(TreeModelListener l) {
		// NOOP
	}

	public boolean isLeaf(Object node) {
		if (node instanceof BoltPluginPrototype<?>) {
			return true;
		}
		return false;
	}

	public Object getRoot() {
		return "Filters";
	}


	public int getIndexOfChild(Object parent, Object child) {

		if (parent instanceof FilterType) {
			@SuppressWarnings("unchecked")
			BoltPluginPrototype<? extends FilterPlugin> plugin = (BoltPluginPrototype<? extends FilterPlugin>) child;
			return FilterPluginManager.SYSTEM.getPlugins().indexOf(plugin);
		} else if (parent instanceof String) {
			FilterType ft = (FilterType) child;
			return ft.ordinal();
		}
		return 0;
	}


	public int getChildCount(Object parent) {

		if (parent instanceof FilterType) {

			FilterType ft = (FilterType) parent;
			int typeCount = 0;

			for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.SYSTEM.getPlugins()) {
				if (plugin.getReferenceInstance().getFilterType() == ft) typeCount++;
			}
			return typeCount;

		} else if (parent instanceof String) {
			return FilterType.values().length;
		}
		return 0;
	}


	public Object getChild(Object parent, int index) {

		if (parent instanceof FilterType) {

			index++;

			FilterType ft = (FilterType) parent;
			int typeCount = 0;

			for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.SYSTEM.getPlugins()) {
				if (plugin.getReferenceInstance().getFilterType() == ft) typeCount++;
				if (typeCount == index) return plugin;
			}

		} else if (parent instanceof String) {
			return FilterType.values()[index];
		}

		return null;

	}


	public void addTreeModelListener(TreeModelListener l) {
		// NOOP
	}
}
