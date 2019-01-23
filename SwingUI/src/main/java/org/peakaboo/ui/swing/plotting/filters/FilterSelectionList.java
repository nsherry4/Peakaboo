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

import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.listcontrols.SelectionListControls;


class FilterSelectionList extends ClearPanel
{

	private FilteringController		controller;
	private FiltersetViewer			owner;

	private JTree					tree;
	
	private SelectionListControls 	controls;

	
	FilterSelectionList(FilteringController _controller, FiltersetViewer _owner)
	{

		this.controller = _controller;
		this.owner = _owner;
		
		
		this.setLayout(new BorderLayout());

		JScrollPane scroller = new JScrollPane(createFilterTree(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(Spacing.bNone());
		
		this.add(scroller, BorderLayout.CENTER);
		add(new TitlePaintedPanel("Add Filter", false, createControls()), BorderLayout.NORTH);

	}


	private JTree createFilterTree()
	{

		TreeModel m = new TreeModel() {

			public void valueForPathChanged(TreePath path, Object newValue)
			{
				// TODO Auto-generated method stub
			}


			public void removeTreeModelListener(TreeModelListener l)
			{
				// TODO Auto-generated method stub
			}


			public boolean isLeaf(Object node)
			{
				if (node instanceof BoltPluginPrototype<?>) {
					return true;
				}
				return false;
			}


			public Object getRoot()
			{
				return "Filters";
			}


			public int getIndexOfChild(Object parent, Object child)
			{

				FilterType ft;

				if (parent instanceof FilterType) {

					ft = (FilterType) parent;
					@SuppressWarnings("unchecked")
					BoltPluginPrototype<? extends FilterPlugin> plugin = (BoltPluginPrototype<? extends FilterPlugin>) child;
					
					return FilterPluginManager.SYSTEM.getPlugins().getAll().indexOf(plugin);
					

				} else if (parent instanceof String) {

					ft = (FilterType) child;
					return ft.ordinal();

				}
				return 0;
			}


			public int getChildCount(Object parent)
			{

				if (parent instanceof FilterType) {

					FilterType ft = (FilterType) parent;
					int typeCount = 0;

					for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.SYSTEM.getPlugins().getAll()) {
						if (plugin.getReferenceInstance().getFilterType() == ft) typeCount++;
					}
					return typeCount;

				} else if (parent instanceof String) {
					return FilterType.values().length;
				}
				return 0;
			}


			public Object getChild(Object parent, int index)
			{

				if (parent instanceof FilterType) {

					index++;

					FilterType ft = (FilterType) parent;
					int typeCount = 0;

					for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.SYSTEM.getPlugins().getAll()) {
						if (plugin.getReferenceInstance().getFilterType() == ft) typeCount++;
						if (typeCount == index) return plugin;
					}

				} else if (parent instanceof String) {

					return FilterType.values()[index];

				}

				return null;

			}


			public void addTreeModelListener(TreeModelListener l)
			{
				// TODO Auto-generated method stub
			}
		};


		tree = new JTree(m);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		
		FilterSelectionRenderer renderer = new FilterSelectionRenderer();
		renderer.setLeafIcon(StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON));
		//renderer.setClosedIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
		//renderer.setOpenIcon(StockIcon.PLACE_FOLDER_OPEN.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		ToolTipManager.sharedInstance().registerComponent(tree);
		
		return tree;
	}


	private JPanel createControls()
	{

		controls = new SelectionListControls("Filter") {
		
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
