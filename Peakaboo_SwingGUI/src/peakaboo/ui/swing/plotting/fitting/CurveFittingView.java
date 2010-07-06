package peakaboo.ui.swing.plotting.fitting;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.fitted.FittingEditor;
import peakaboo.ui.swing.plotting.fitting.fitted.FittingRenderer;
import peakaboo.ui.swing.plotting.fitting.summation.SummationPanel;
import peakaboo.ui.swing.plotting.fitting.unfitted.ProposalEditor;
import peakaboo.ui.swing.plotting.fitting.unfitted.ProposalRenderer;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.listcontrols.ListControlButton;
import swidget.widgets.listcontrols.ListControls;
import swidget.widgets.listcontrols.SelectionListControls;
import swidget.widgets.listcontrols.ListControls.ElementCount;




public class CurveFittingView extends ClearPanel
{

	protected FittingController		controller;

	private final String			FITTED		= "Fitted";
	private final String			UNFITTED	= "Unfitted";
	private final String			SUMMATION	= "Summation";

	protected JTable				fitTable;
	protected MutableTableModel		tm;
	protected MutableTreeModel		utm;
	protected JTree					unfitTree;

	protected JPanel				cardPanel;
	protected CardLayout			card;
	protected SummationPanel		summationPanel;

	protected ListControls			controls;
	private SelectionListControls	selControls;


	public String getName()
	{
		return "Peak Fitting";
	}


	public CurveFittingView(FittingController _controller)
	{
		super();

		this.controller = _controller;

		setPreferredSize(new Dimension(200, getPreferredSize().height));

		JPanel fittedPanel = createFittedElementsPanel();
		JPanel unusedTable = createUnfittedElementsPanel();
		JPanel summationPanel = createSummationPanel();

		cardPanel = createCardPanel(fittedPanel, unusedTable, summationPanel);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{

				fitTable.invalidate();
				//fitTable.invalidate();
				unfitTree.invalidate();
				
				//tm.fireChangeEvent();
				//utm.fireChangeEvent();
				

				int elements = controller.getFittedTransitionSeries().size();
				if (elements == 0)
				{
					controls.setElementCount(ListControls.ElementCount.NONE);
				}
				else if (elements == 1)
				{
					controls.setElementCount(ListControls.ElementCount.ONE);
				}
				else
				{
					controls.setElementCount(ListControls.ElementCount.MANY);
				}

			}
		});

	}


	private JPanel createCardPanel(JPanel t1, JPanel t2, JPanel t3)
	{
		JPanel panel = new ClearPanel();
		card = new CardLayout();
		panel.setLayout(card);

		panel.add(t1, FITTED);
		panel.add(t2, UNFITTED);
		panel.add(t3, SUMMATION);

		return panel;
	}


	private JPanel createFittedElementsPanel()
	{

		JPanel panel = new ClearPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		JScrollPane fitted = createTable();
		panel.add(fitted, BorderLayout.CENTER);

		String tooltips[] = {
				"Add elemental fittings",
				"Remove the selected fitting",
				"Clear all fittings",
				"Move the selected fitting up",
				"Move the selected fitting down" };
		controls = new ListControls(tooltips) {

			@Override
			public void up()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.moveTransitionSeriesUp(ts);
				tm.fireChangeEvent();
				row = controller.getFittedTransitionSeries().indexOf(ts);
				fitTable.addRowSelectionInterval(row, row);
			}


			@Override
			public void remove()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.removeTransitionSeries(ts);
				tm.fireChangeEvent();
				utm.fireChangeEvent();
			}


			@Override
			public void down()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.moveTransitionSeriesDown(ts);
				row = controller.getFittedTransitionSeries().indexOf(ts);
				fitTable.addRowSelectionInterval(row, row);
			}


			@Override
			public void clear()
			{
				controller.clearTransitionSeries();
				tm.fireChangeEvent();
				utm.fireChangeEvent();
			}


			@Override
			public void add()
			{
				card.show(cardPanel, UNFITTED);
				tm.fireChangeEvent();
				utm.fireChangeEvent();

				unfitTree.setSelectionRow(0);

			}

		};

		ListControlButton cross = new ListControlButton("cross", "Summation", "Add a summation fitting") {

			@Override
			public void setEnableState(ElementCount ec)
			{
				setEnabled(ec != ElementCount.NONE);
			}
		};
		cross.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				summationPanel.resetSelectors();
				card.show(cardPanel, SUMMATION);
				tm.fireChangeEvent();
				
			}
		});
		controls.addButton(cross, 1);

		// panel.add(controls, BorderLayout.SOUTH);

		panel.add(controls, BorderLayout.NORTH);

		return panel;
	}


	private JPanel createUnfittedElementsPanel()
	{

		selControls = new SelectionListControls("Elements") {

			@Override
			protected void cancel()
			{
				controller.clearProposedTransitionSeries();
				card.show(cardPanel, FITTED);
				fitTable.invalidate();
				unfitTree.invalidate();
			}


			@Override
			protected void approve()
			{
				controller.commitProposedTransitionSeries();
				card.show(cardPanel, FITTED);

				fitTable.invalidate();
				tm.fireChangeEvent();

				unfitTree.invalidate();
				utm.fireChangeEvent();
			}
		};

		JPanel panel = new ClearPanel();
		panel.setLayout(new BorderLayout());

		JScrollPane fitted = createUnfittedTable();
		panel.add(fitted, BorderLayout.CENTER);

		// panel.setBorder(new RoundedTitleBorder("Add New Elements", new Color(0f, 0f, 0f, 0.3f), new Color(0f, 0f, 0f,
		// 0f)));
		// panel.add(selControls, BorderLayout.SOUTH);
		panel.add(new TitleGradientPanel("Select New Elements", true, selControls), BorderLayout.NORTH);

		return panel;
	}


	private JPanel createSummationPanel()
	{

		summationPanel = new SummationPanel(controller);
		
		selControls = new SelectionListControls("Summation") {

			@Override
			protected void cancel()
			{
				//controller.clearProposedTransitionSeries();
				summationPanel.resetSelectors();
				card.show(cardPanel, FITTED);
				fitTable.invalidate();
				
				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();
				
				//unfitTree.invalidate();
			}


			@Override
			protected void approve()
			{
				controller.addTransitionSeries(summationPanel.getTransitionSeries());
				card.show(cardPanel, FITTED);

				fitTable.invalidate();
				tm.fireChangeEvent();
				
				controller.clearProposedTransitionSeries();
				controller.fittingProposalsInvalidated();

				//unfitTree.invalidate();
				//utm.fireChangeEvent();
			}
		};

		JPanel panel = new ClearPanel();
		panel.setLayout(new BorderLayout());

		JScrollPane scroll = new JScrollPane(summationPanel);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBorder(Spacing.bMedium());
		
		panel.add(scroll, BorderLayout.CENTER);

		// panel.setBorder(new RoundedTitleBorder("Add New Elements", new Color(0f, 0f, 0f, 0.3f), new Color(0f, 0f, 0f,
		// 0f)));
		// panel.add(selControls, BorderLayout.SOUTH);
		panel.add(new TitleGradientPanel("Specify New Summation", true, selControls), BorderLayout.NORTH);

		

		
		return panel;

	}


	private JScrollPane createTable()
	{

		//fitTable = new JTree();
		
		fitTable = new JTable();
		
		tm = new MutableTableModel() {
			
			List<TableModelListener> listeners;
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{

				if (columnIndex == 0)
				{
					Boolean visible = (Boolean)aValue;
					controller.setTransitionSeriesVisibility( controller.getFittedTransitionSeries().get(rowIndex), visible );
				}
					
			}
			
		
			public void removeTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TableModelListener> list();
				listeners.remove(l);
			}
			
		
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return columnIndex == 0;
			}
			
		
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0)
				{
					return controller.getFittedTransitionSeries().get(rowIndex).visible;
				} else {
					return controller.getFittedTransitionSeries().get(rowIndex);
				}
			}
			
		
			public int getRowCount()
			{
				return controller.getFittedTransitionSeries().size();
			}
			
		
			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) return "Fit";
				return "Transition Series";
				
			}
			
		
			public int getColumnCount()
			{
				return 2;
			}
			
		
			public Class<?> getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 0: return Boolean.class;
					case 1: return TransitionSeries.class;
					default: return Object.class;
				}
			}
			
		
			public void addTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TableModelListener> list();
				listeners.add(l);
			}


			public void fireChangeEvent()
			{
				for(TableModelListener l : listeners)
				{
					l.tableChanged(new TableModelEvent(this));
				}
			}
		};
		
		
		fitTable.setModel(tm);
		

		FittingRenderer renderer = new FittingRenderer(controller);
		fitTable.getColumnModel().getColumn(1).setCellRenderer(new FittingRenderer(controller));

		fitTable.setShowVerticalLines(false);
		fitTable.setShowHorizontalLines(false);
		
			
		TableColumn column = fitTable.getColumnModel().getColumn(0);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);
		
		fitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scroll = new JScrollPane(fitTable);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));

		return scroll;
	}


	/*
	 * 
	 * private JScrollPane createUnfittedTable() {
	 * 
	 * TableModel m = new TableModel() {
	 * 
	 * public void setValueAt(Object value, int rowIndex, int columnIndex) { if (columnIndex == 0) { boolean add =
	 * ((Boolean) value).booleanValue(); TransitionSeries e = controller.getUnfittedTransitionSeries().get(rowIndex); if
	 * (add) { controller.addProposedTransitionSeries(e); } else { controller.removeProposedTransitionSeries(e); } }
	 * else { return; } }
	 * 
	 * 
	 * public void removeTableModelListener(TableModelListener l) { // TODO Auto-generated method stub }
	 * 
	 * 
	 * public boolean isCellEditable(int rowIndex, int columnIndex) { if (columnIndex == 0) return true; return false;
	 * 
	 * }
	 * 
	 * 
	 * public Object getValueAt(int rowIndex, int columnIndex) { TransitionSeries ts; if (columnIndex == 0) { ts =
	 * controller.getUnfittedTransitionSeries().get(rowIndex); return
	 * (controller.getProposedTransitionSeries().indexOf(ts) != -1); } else { ts =
	 * controller.getUnfittedTransitionSeries().get(rowIndex); return ts.element.ordinal() + ": " + ts.element.name() +
	 * " (" + ts.element.toString() + ")"; } }
	 * 
	 * 
	 * public int getRowCount() { return controller.getUnfittedTransitionSeries().size(); }
	 * 
	 * 
	 * public String getColumnName(int columnIndex) { if (columnIndex == 0) { return "Add"; } else { return
	 * "Unfitted Elements"; } }
	 * 
	 * 
	 * public int getColumnCount() { return 2; }
	 * 
	 * 
	 * public Class<?> getColumnClass(int columnIndex) { if (columnIndex == 0) { return Boolean.class; } else { return
	 * Element.class; } }
	 * 
	 * 
	 * public void addTableModelListener(TableModelListener l) { // TODO Auto-generated method stub } };
	 * 
	 * 
	 * unfitTable = new JTable(m); unfitTable.setShowGrid(false);
	 * 
	 * TableColumn column = null; column = unfitTable.getColumnModel().getColumn(0); column.setPreferredWidth(40);
	 * column.setMaxWidth(100);
	 * 
	 * 
	 * JScrollPane scroll = new JScrollPane(unfitTable);
	 * scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); scroll.setPreferredSize(new
	 * Dimension(200, 0));
	 * 
	 * 
	 * return scroll; }
	 */

	private JScrollPane createUnfittedTable()
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
				if (listeners == null) listeners = DataTypeFactory.<TreeModelListener> list();
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
				if (listeners == null) listeners = DataTypeFactory.<TreeModelListener> list();
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

		ProposalRenderer renderer = new ProposalRenderer(controller);
		unfitTree.setCellRenderer(renderer);
		unfitTree.setEditable(true);
		unfitTree.setCellEditor(new ProposalEditor(unfitTree, renderer, controller));

		/*
		 * BasicTreeUI basicTreeUI = (BasicTreeUI) unfitTable.getUI();
		 * basicTreeUI.setRightChildIndent((int)Math.round(basicTreeUI.getRightChildIndent() * 1.5));
		 */

		JScrollPane scroll = new JScrollPane(unfitTree);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));

		return scroll;
	}

}
