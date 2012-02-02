package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.AbstractFilter.FilterType;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.SelectionListControls;


public class FilterSelectionViewer extends ClearPanel
{

	protected IFilteringController	controller;
	protected FiltersetViewer		owner;

	protected JTree					tree;
	
	private SelectionListControls 	controls;


	public FilterSelectionViewer(IFilteringController _controller, FiltersetViewer _owner)
	{

		this.controller = _controller;
		this.owner = _owner;

		this.setLayout(new BorderLayout());

		JScrollPane scroller = new JScrollPane(createFilterTree(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(scroller, BorderLayout.CENTER);
		add(new TitleGradientPanel("Add Filter", true, createControls()), BorderLayout.NORTH);

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
				if (node instanceof AbstractFilter) {
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
					AbstractFilter filter = (AbstractFilter) child;
					
					return controller.getAvailableFilters().indexOf(filter);
					

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

					for (AbstractFilter f : controller.getAvailableFilters()) {
						if (f.getFilterType() == ft) typeCount++;
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

					for (AbstractFilter f : controller.getAvailableFilters()) {
						if (f.getFilterType() == ft) typeCount++;
						if (typeCount == index) return f;
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
		renderer.setClosedIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
		renderer.setOpenIcon(StockIcon.PLACE_FOLDER_OPEN.toImageIcon(IconSize.BUTTON));
		tree.setCellRenderer(renderer);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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

				if (leaf instanceof AbstractFilter) {
					AbstractFilter filter = (AbstractFilter) leaf;
					//filter = AbstractFilter.createNewInstance(filter);
					//filter.initialize();
					controller.addFilter(filter.getFilterName());
				}

				owner.showEditPane();
			}
		};
		controls.setOpaque(false);
		
		return controls;

	}

}
