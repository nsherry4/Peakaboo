package peakaboo.ui.swing.fitting.tableview;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.gradientpanel.TitleGradientPanel;
import peakaboo.ui.swing.widgets.listcontrols.ListControls;
import peakaboo.ui.swing.widgets.listcontrols.SelectionListControls;

public class CurveFittingView extends ClearPanel
{

	protected FittingController		controller;

	private final String			FITTED		= "Fitted";
	private final String			UNFITTED	= "Unfitted";

	protected JTable					fitTable;
	protected JTable					unfitTable;

	protected JPanel				cardPanel;
	protected CardLayout			card;

	protected ListControls			controls;
	private SelectionListControls 	selControls;


	public CurveFittingView(FittingController _controller)
	{
		super();
		
		this.controller = _controller;

		JPanel fittedPanel = createFittedElementsPanel();
		JPanel unusedTable = createUnfittedElementsPanel();

		cardPanel = createCardPanel(fittedPanel, unusedTable);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				
				fitTable.invalidate();
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
				int row = fitTable.getSelectedRow();
				Element element = controller.getFittedElements().get(row);
				controller.moveElementUp(element);
				row = controller.getFittedElements().indexOf(element);
				fitTable.addRowSelectionInterval(row, row);

				fitTable.invalidate();
			}
		
		
			@Override
			public void remove()
			{
				int row = fitTable.getSelectedRow();
				if (row >= controller.getFittedElements().size()) return;
				Element element = controller.getFittedElements().get(row);
				controller.removeElement(element);
				fitTable.invalidate();
			}
		
		
			@Override
			public void down()
			{
				int row = fitTable.getSelectedRow();
				Element element = controller.getFittedElements().get(row);
				controller.moveElementDown(element);
				row = controller.getFittedElements().indexOf(element);
				fitTable.addRowSelectionInterval(row, row);

				fitTable.invalidate();
			}
		
		
			@Override
			public void clear()
			{
				controller.clearElements();
				fitTable.invalidate();
			}
		
		
			@Override
			public void add()
			{
				card.show(cardPanel, UNFITTED);
				unfitTable.invalidate();
			}
		};
		
		
		//panel.add(controls, BorderLayout.SOUTH);
		
		panel.add(new TitleGradientPanel("Fitted Elements", true, controls), BorderLayout.NORTH);
		



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
				fitTable.invalidate();
			}
		
		
			@Override
			protected void approve()
			{
				controller.commitProposedElements();
				card.show(cardPanel, FITTED);
				fitTable.invalidate();
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

		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {
					boolean show = ((Boolean) value).booleanValue();
					Element e = controller.getFittedElements().get(rowIndex);
					controller.setElementVisibility(e, show);
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
				if (columnIndex == 0) {
					return true;
				} else {
					return false;
				}
			}


			public Object getValueAt(int rowIndex, int columnIndex)
			{

				if (columnIndex == 0) {
					Element e = controller.getFittedElements().get(rowIndex);
					return controller.getElementVisibility(e);
				} else {
					List<Element> list;
					list = controller.getFittedElements();
					if (rowIndex >= list.size()) return null;
					Element e = list.get(rowIndex);
					return e;
				}
			}


			public int getRowCount()
			{
				return controller.getFittedElements().size();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) {
					return "Fit";
				} else {
					return "Element Fitting Priority";
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



		fitTable = new JTable(m);
		fitTable.setShowGrid(false);
		fitTable.setShowHorizontalLines(true);

		fitTable.setDefaultRenderer(Element.class, new ElementRenderer());

		// fitTable.setRowHeight(fitTable.getRowHeight() * 2);
		// fitTable.setRowHeight(new TestRenderer().getHeight());
		fitTable.setFont(fitTable.getFont().deriveFont((fitTable.getFont().getSize2D() * 1.5f)));

		TableColumn column = null;
		column = fitTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		fitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scroll = new JScrollPane(fitTable);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
				if (columnIndex == 0) {
					return true;
				} else {
					return false;
				}
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
