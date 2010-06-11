package peakaboo.ui.swing.fitting;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.gradientpanel.TitleGradientPanel;
import peakaboo.ui.swing.widgets.listcontrols.ListControls;
import peakaboo.ui.swing.widgets.listcontrols.SelectionListControls;

public class CurveFittingView extends ClearPanel
{

	protected FittingController		controller;

	private final String			FITTED		= "Fitted";
	private final String			UNFITTED	= "Unfitted";

	protected JTree					fitTree;
	protected MutableTreeModel		tm;
	protected JTable				unfitTable;

	protected JPanel				cardPanel;
	protected CardLayout			card;

	protected ListControls			controls;
	private SelectionListControls 	selControls;

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

		cardPanel = createCardPanel(fittedPanel, unusedTable);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				
				//fitTable.invalidate();
				fitTree.invalidate();
				unfitTable.invalidate();
				

				int elements = controller.getFittedElements().size();
				if ( elements == 0 ){
					controls.setElementCount(ListControls.ElementCount.NONE);
				} else if ( elements == 1 ){
					controls.setElementCount(ListControls.ElementCount.ONE);
				} else {
					controls.setElementCount(ListControls.ElementCount.MANY);
				}


			}
		});


	}


	private JPanel createCardPanel(JPanel t1, JPanel t2)
	{
		JPanel panel = new ClearPanel();
		card = new CardLayout();
		panel.setLayout(card);

		panel.add(t1, FITTED);
		panel.add(t2, UNFITTED);

		return panel;
	}


	private JPanel createFittedElementsPanel()
	{
		
		JPanel panel = new ClearPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		JScrollPane fitted = createTable();
		panel.add(fitted, BorderLayout.CENTER);


		controls = new ListControls() {
		
			@Override
			public void up()
			{
				fitTree.cancelEditing();
				Object c = fitTree.getSelectionPath().getLastPathComponent();
				if (c instanceof Element){
					Element element = (Element)c;
					controller.moveElementUp(element);
					tm.fireChangeEvent();
					fitTree.setSelectionRow(controller.getFittedElements().indexOf(element));
				}
			}
		
		
			@Override
			public void remove()
			{
				fitTree.cancelEditing();
				if (fitTree.getSelectionPath() == null) return;
				Object c = fitTree.getSelectionPath().getLastPathComponent();
				if (c instanceof Element){
					Element element = (Element)c;
					controller.removeElement(element);
					tm.fireChangeEvent();
				}
			}
		
		
			@Override
			public void down()
			{
				fitTree.cancelEditing();
				Object c = fitTree.getSelectionPath().getLastPathComponent();
				if (c instanceof Element){
					Element element = (Element)c;
					controller.moveElementDown(element);
					tm.fireChangeEvent();
					fitTree.setSelectionRow(controller.getFittedElements().indexOf(element));
					
				}
			}
		
		
			@Override
			public void clear()
			{
				fitTree.cancelEditing();
				controller.clearElements();
				tm.fireChangeEvent();
			}
		
		
			@Override
			public void add()
			{
				fitTree.cancelEditing();
				card.show(cardPanel, UNFITTED);
				tm.fireChangeEvent();
			}
			
		};
		
		
		//panel.add(controls, BorderLayout.SOUTH);
		
		panel.add(controls, BorderLayout.NORTH);
		



		return panel;
	}


	private JPanel createUnfittedElementsPanel()
	{

		selControls = new SelectionListControls("Elements") {
		
			@Override
			protected void cancel()
			{
				controller.clearProposedElements();
				card.show(cardPanel, FITTED);
				fitTree.invalidate();
			}
		
		
			@Override
			protected void approve()
			{
				controller.commitProposedElements();
				card.show(cardPanel, FITTED);
				fitTree.invalidate();
				fitTree.setModel(tm);
				tm.fireChangeEvent();
			}
		};
		
		
		
		JPanel panel = new ClearPanel();
		panel.setLayout(new BorderLayout());

		JScrollPane fitted = createUnfittedTable();
		panel.add(fitted, BorderLayout.CENTER);


		//panel.setBorder(new RoundedTitleBorder("Add New Elements", new Color(0f, 0f, 0f, 0.3f), new Color(0f, 0f, 0f, 0f)));
		//panel.add(selControls, BorderLayout.SOUTH);
		panel.add(new TitleGradientPanel("Select New Elements", true, selControls), BorderLayout.NORTH);

		return panel;
	}


	private JScrollPane createTable()
	{
		
		tm = new MutableTreeModel() {
		
			private List<TreeModelListener> listeners;
			
			public void valueForPathChanged(TreePath path, Object newValue)
			{		
				if (path.getLastPathComponent() instanceof TransitionSeries){
					TransitionSeries ts = (TransitionSeries)path.getLastPathComponent();
					ts.visible = (Boolean)newValue;
					controller.fittingDataInvalidated();
				} else if (path.getLastPathComponent() instanceof Element){
					Element element = (Element)path.getLastPathComponent();
					controller.setElementVisibility(element, (Boolean)newValue);
				}
			}
		
		
			public void removeTreeModelListener(TreeModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TreeModelListener>list();
				listeners.remove(l);
			}
		
		
			public boolean isLeaf(Object node)
			{

				if (node instanceof TransitionSeries){
					return true;
				} else if (node instanceof Element) {
					if (  controller.getTransitionSeriesTypesForElement((Element)node, true).size() <= 1) return true;
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
				if (parent instanceof Element){
					TransitionSeries ts = (TransitionSeries)child;
					return ts.type.ordinal();
				} else if (parent instanceof String){
					Element e = (Element)child;
					return controller.getFittedElements().indexOf(e);
				}
				return 0;
			}
		
		
			public int getChildCount(Object parent)
			{
				if (parent instanceof Element){
					return controller.getTransitionSeriesTypesForElement((Element)parent, true).size();
				} else if (parent instanceof String){
					return controller.getFittedElements().size();
				}
				return 0;
			}
		
		
			public Object getChild(Object parent, int index)
			{
				
				if (parent instanceof String){
					return controller.getFittedElements().get(index);
				} else if (parent instanceof Element){
					TransitionSeriesType type = controller.getTransitionSeriesTypesForElement((Element)parent, true).get(index);
					return controller.getTransitionSeriesForElement((Element)parent, type);
				}
				return null;
			}
		
		
			public void addTreeModelListener(TreeModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TreeModelListener>list();
				listeners.add(l);
		
			}

			public void fireChangeEvent()
			{
				for (TreeModelListener tml : listeners){
					tml.treeStructureChanged(  new TreeModelEvent( this, new TreePath(getRoot()) )  );
				}
			}

		};
		
		
		fitTree = new JTree(tm); 
		fitTree.setShowsRootHandles(true);
		fitTree.setRootVisible(false);
		
		FittingRenderer renderer = new FittingRenderer(controller);
		fitTree.setCellRenderer(renderer);
		fitTree.setEditable(true);
		fitTree.setCellEditor(new FittingEditor(renderer, controller));
		
		BasicTreeUI basicTreeUI = (BasicTreeUI) fitTree.getUI();
		basicTreeUI.setRightChildIndent((int)Math.round(basicTreeUI.getRightChildIndent() * 1.5));

		
		JScrollPane scroll = new JScrollPane(fitTree);
		//scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));
		

		return scroll;
	}


	private JScrollPane createUnfittedTable()
	{

		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {
					boolean add = ((Boolean) value).booleanValue();
					Element e = controller.getUnfittedElements().get(rowIndex);
					if (add) {
						controller.addProposedElement(e);
					} else {
						controller.removeProposedElement(e);
					}
				} else {
					return;
				}
			}


			public void removeTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub
			}


			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) return true;
				return false;
				
			}


			public Object getValueAt(int rowIndex, int columnIndex)
			{
				Element e;
				if (columnIndex == 0) {
					e = controller.getUnfittedElements().get(rowIndex);
					return (controller.getProposedElements().indexOf(e) != -1);
				} else {
					e = controller.getUnfittedElements().get(rowIndex);
					return (e.ordinal() + 1) + ": " + e.name() + " (" + e.toString() + ")";
				}
			}


			public int getRowCount()
			{
				return controller.getUnfittedElements().size();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) {
					return "Add";
				} else {
					return "Unfitted Elements";
				}
			}


			public int getColumnCount()
			{
				return 2;
			}


			public Class<?> getColumnClass(int columnIndex)
			{
				if (columnIndex == 0) {
					return Boolean.class;
				} else {
					return Element.class;
				}
			}


			public void addTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub
			}
		};


		unfitTable = new JTable(m);
		unfitTable.setShowGrid(false);

		TableColumn column = null;
		column = unfitTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);


		JScrollPane scroll = new JScrollPane(unfitTable);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));


		return scroll;
	}

}
