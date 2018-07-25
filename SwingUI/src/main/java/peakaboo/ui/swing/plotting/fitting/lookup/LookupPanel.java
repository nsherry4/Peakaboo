package peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.listcontrols.SelectionListControls;



public class LookupPanel extends ClearPanel implements Changeable
{

	private MutableTreeModel		utm;
	private JTree					unfitTree;
	private JTextField				search;
	private String					query = "";
	private List<Element>			filtered = Arrays.asList(Element.values());
	
	private FittingController		controller;

	private SelectionListControls	selControls;


	public LookupPanel(final FittingController controller, final CurveFittingView owner)
	{

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

		this.add(elementListPanel(), BorderLayout.CENTER);
		this.add(new TitlePaintedPanel("Element Lookup", false, selControls), BorderLayout.NORTH);


	}
	
	private JPanel elementListPanel() {
		
		JPanel panel = new JPanel(new BorderLayout());

		JScrollPane fitted = createLookupTable();
		panel.add(fitted, BorderLayout.CENTER);
		
		search = new FilterBox();
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(search, BorderLayout.CENTER);
		searchPanel.setBorder(new EmptyBorder(0, 0, 1, 0));
		panel.add(searchPanel, BorderLayout.NORTH);

		search.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (!search.getText().equals(query)) {
					query = search.getText().toLowerCase();
					filter();
					utm.fireStructureChangeEvent();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		search.addActionListener(e -> {
			changed();
		});
		
		return panel;
		
	}
	
	private boolean match(Element e) {
		if ((e.name().toLowerCase()).contains(query)) return true;
		if ((e.toString().toLowerCase()).contains(query)) return true;
		if ((e.atomicNumber()+"").toLowerCase().contains(query)) return true;
		
		return false;
	}

	private void filter() {
		filtered = Arrays.asList(Element.values()).stream().filter(this::match).collect(Collectors.toList());
	}

	public void changed()
	{
		utm.fireStructureChangeEvent();
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
				else if (path.getLastPathComponent() instanceof Element)
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
				else if (node instanceof Element)
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
					Element e = (Element) child;
					return filtered.indexOf(e);
				}
				else if (parent instanceof Element)
				{
					TransitionSeries ts = (TransitionSeries) child;
					Element e = (Element) parent;
					List<TransitionSeries> ofElement = controller.getUnfittedTransitionSeries().stream().filter(t -> t.element == ts.element).collect(Collectors.toList());
					return ofElement.indexOf(ts);
										
				}
				return 0;
			}


			public int getChildCount(Object parent)
			{
				if (parent instanceof Element)
				{
					Element e = (Element) parent;
					return controller.getUnfittedTransitionSeries().stream().filter(t -> t.element == e).collect(Collectors.toList()).size();
				}
				else if (parent instanceof String)
				{
					return filtered.size();
				}
				return 0;
			}


			public Object getChild(Object parent, int index)
			{

				if (parent instanceof String)
				{
					return filtered.get(index);
				}
				else if (parent instanceof Element)
				{

					Element e = (Element) parent;
					return controller.getUnfittedTransitionSeries().stream().filter(t -> t.element == e).collect(Collectors.toList()).get(index);

				}
				return null;
			}


			public void addTreeModelListener(TreeModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TreeModelListener>();
				listeners.add(l);

			}


			public void fireNodesChangeEvent()
			{
				for (TreeModelListener tml : listeners)
				{
					tml.treeNodesChanged(new TreeModelEvent(this, new TreePath(getRoot())));
				}
			}
			
			public void fireStructureChangeEvent()
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
