package peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.curvefit.transition.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.fitting.MutableTreeModel;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class LookupPanel extends ClearPanel implements Changeable
{

	protected MutableTreeModel		utm;
	protected JTree					unfitTree;

	CurveFittingView				owner;
	FittingController				controller;

	private SelectionListControls	selControls;


	public LookupPanel(final FittingController controller, final CurveFittingView owner)
	{

		this.owner = owner;
		this.controller = controller;

		selControls = new SelectionListControls("Fittings") {

			@Override
			protected void cancel()
			{
				controller.clearProposedTransitionSeries();
				owner.dialogClose();
			}


			@Override
			protected void approve()
			{
				controller.commitProposedTransitionSeries();
				owner.dialogClose();
			}
		};
		selControls.setOpaque(false);
		
		this.setLayout(new BorderLayout());

		JScrollPane fitted = createLookupTable();
		this.add(fitted, BorderLayout.CENTER);

		this.add(new TitlePaintedPanel("Element Lookup", false, selControls), BorderLayout.NORTH);


	}


	public void changed()
	{
		utm.fireChangeEvent();
		//unfitTree.setSelectionRow(0);
	}


	private JScrollPane createLookupTable()
	{

		utm = new MutableTreeModel() {

			private List<TreeModelListener>	listeners;


			public void valueForPathChanged(TreePath path, Object newValue)
			{
				if (path.getLastPathComponent() instanceof TransitionSeries)
				{
					TransitionSeries ts = (TransitionSeries) path.getLastPathComponent();
					boolean included = (Boolean) newValue;

					if (included)
					{
						controller.addProposedTransitionSeries(ts);
					}
					else
					{
						controller.removeProposedTransitionSeries(ts);
					}

					controller.fittingProposalsInvalidated();

				}
				else if (path.getLastPathComponent() instanceof TransitionSeriesType)
				{
					// do nothing
				}
			}


			public void removeTreeModelListener(TreeModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TreeModelListener>();
				listeners.remove(l);
			}


			public boolean isLeaf(Object node)
			{

				if (node instanceof TransitionSeries)
				{
					return true;
				}
				else if (node instanceof TransitionSeriesType)
				{
					return false;
				}
				return false;
			}


			public Object getRoot()
			{
				return "Fittings";
			}


			public int getIndexOfChild(Object parent, Object child)
			{
				if (parent instanceof String)
				{
					TransitionSeriesType tst = (TransitionSeriesType) child;
					return tst.ordinal();
				}
				else if (parent instanceof TransitionSeriesType)
				{
					TransitionSeries ts = (TransitionSeries) child;
					TransitionSeriesType tst = (TransitionSeriesType) parent;
					return controller.getUnfittedTransitionSeries(tst).indexOf(ts);
				}
				return 0;
			}


			public int getChildCount(Object parent)
			{
				if (parent instanceof TransitionSeriesType)
				{
					return controller.getUnfittedTransitionSeries((TransitionSeriesType) parent).size();
				}
				else if (parent instanceof String)
				{
					return TransitionSeriesType.values().length - 1;
				}
				return 0;
			}


			public Object getChild(Object parent, int index)
			{

				if (parent instanceof String)
				{
					return TransitionSeriesType.values()[index];
				}
				else if (parent instanceof TransitionSeriesType)
				{

					TransitionSeriesType type = (TransitionSeriesType) parent;
					List<TransitionSeries> tss = controller.getUnfittedTransitionSeries(type);
					return tss.get(index);

				}
				return null;
			}


			public void addTreeModelListener(TreeModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TreeModelListener>();
				listeners.add(l);

			}


			public void fireChangeEvent()
			{
				for (TreeModelListener tml : listeners)
				{
					tml.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot())));
				}
			}

		};

		unfitTree = new JTree(utm);
		unfitTree.setShowsRootHandles(true);
		unfitTree.setRootVisible(false);

		LookupRenderer renderer = new LookupRenderer(controller);
		
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		
		unfitTree.setCellRenderer(renderer);
		unfitTree.setEditable(true);
		unfitTree.setCellEditor(new LookupEditor(unfitTree, renderer, controller));

		/*
		 * BasicTreeUI basicTreeUI = (BasicTreeUI) unfitTable.getUI();
		 * basicTreeUI.setRightChildIndent((int)Math.round(basicTreeUI.getRightChildIndent() * 1.5));
		 */

		JScrollPane scroll = new JScrollPane(unfitTree);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBorder(Spacing.bNone());

		return scroll;
	}

}
