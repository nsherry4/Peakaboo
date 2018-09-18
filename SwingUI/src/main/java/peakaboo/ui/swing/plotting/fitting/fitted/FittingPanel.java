package peakaboo.ui.swing.plotting.fitting.fitted;



import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.ui.swing.plotting.PlotPanel;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.fitting.MutableTableModel;
import scitypes.util.Mutable;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;
import swidget.widgets.listcontrols.ListControls;
import swidget.widgets.listcontrols.ReorderTransferHandler;



public class FittingPanel extends ClearPanel implements Changeable
{

	private ListControls				controls;
	private JTable						fitTable;

	private MutableTableModel			tm;

	private CurveFittingView			owner;
	private FittingController			controller;
	private PlotPanel 					plotPanel;


	public FittingPanel(final FittingController controller, final CurveFittingView owner, PlotPanel plotPanel)
	{

		this.owner = owner;
		this.controller = controller;
		this.plotPanel = plotPanel;
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());

		JScrollPane fitted = createFittedTable();
		this.add(fitted, BorderLayout.CENTER);

		JPopupMenu addMenu = createAddMenu();

		ImageButton addButton = new ImageButton(StockIcon.EDIT_ADD).withTooltip("Add Fittings");
		addButton.withAction(() -> {
			addMenu.show(addButton, 0, addButton.getHeight());
		});
		
		ImageButton removeButton = new ImageButton(StockIcon.EDIT_REMOVE).withTooltip("Remove Selected Fittings").withAction(() -> {
			int rows[] = fitTable.getSelectedRows();
			List<TransitionSeries> tss = Arrays.stream(rows).boxed().map(i -> controller.getFittedTransitionSeries().get(i)).collect(toList());
				
			if (tss.size() == 0) return;
			for (TransitionSeries ts : tss)
			{
				controller.removeTransitionSeries(ts);
			}
			owner.changed();
		});
		
		ImageButton clearButton = new ImageButton(StockIcon.EDIT_CLEAR).withTooltip("Clear All Fittings").withAction(() -> {
			controller.clearTransitionSeries();
			owner.changed();
		});
		
		controls = new ListControls(addButton, removeButton, clearButton);

		
		this.add(controls, BorderLayout.NORTH);

	}

	private List<TransitionSeries> getSelected() {		
		if (! fitTable.hasFocus()) { return Collections.emptyList(); }
		List<TransitionSeries> selected = new ArrayList<>();
		for (int i : fitTable.getSelectedRows()) {
			selected.add(controller.getFittedTransitionSeries().get(i));
		}
		return selected;
	}

	public void changed()
	{
		tm.fireChangeEvent();
		
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
	
	
	private JPopupMenu createAddMenu()
	{
		JPopupMenu menu = new JPopupMenu();

		JMenuItem elementalAddItem = new JMenuItem("Element Lookup");
		elementalAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.elementalAdd();
			}
		});

		JMenuItem summationAddItem = new JMenuItem("Summation Fitting");
		summationAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.summationAdd();
			}
		});
		
		JMenuItem smartAddItem = new JMenuItem("Guided Fitting");
		smartAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.guidedAdd();
			}
		});
		
		JMenuItem autoAddItems = new JMenuItem("Auto Fitting");
		autoAddItems.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.autoAdd();
			}
		});
		
				

		menu.add(smartAddItem);
		menu.add(autoAddItems);
		menu.add(elementalAddItem);		
		menu.add(summationAddItem);
		


		return menu;
	}


	private JScrollPane createFittedTable()
	{

		fitTable = new JTable();

		tm = new MutableTableModel() {

			List<TableModelListener>	listeners;


			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{

				if (columnIndex == 0)
				{
					Boolean visible = (Boolean) aValue;
					controller.setTransitionSeriesVisibility(
							controller.getFittedTransitionSeries().get(rowIndex),
							visible);
				}

			}


			public void removeTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TableModelListener>();
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
				}
				else
				{
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
					case 0:
						return Boolean.class;
					case 1:
						return TransitionSeries.class;
					default:
						return Object.class;
				}
			}


			public void addTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TableModelListener>();
				listeners.add(l);
			}


			public void fireChangeEvent()
			{
				for (TableModelListener l : listeners)
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
		fitTable.setFillsViewportHeight(true);
		fitTable.setTableHeader(null);

		fitTable.setRowHeight(renderer.getPreferredSize().height);

		TableColumn column = fitTable.getColumnModel().getColumn(0);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		fitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		

		//On selection change or focus change, update the controller's list of highlighted fits
		fitTable.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) { return; }
			controller.setHighlightedTransitionSeries(getSelected());
		});
		fitTable.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (e.isTemporary()) { return; }
				controller.setHighlightedTransitionSeries(getSelected());
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (e.isTemporary()) { return; }
				controller.setHighlightedTransitionSeries(getSelected());
			}
		});
		
		
		fitTable.setDragEnabled(true);
		fitTable.setDropMode(DropMode.INSERT_ROWS);
		fitTable.setTransferHandler(new ReorderTransferHandler(fitTable) {
			
			@Override
			public void move(int from, int to) {
				controller.moveTransitionSeries(from, to);
				
				//update selection
				int index = -1;
				if (from > to) {
					index = to;
				} else {
					index = to-1;
				}
				fitTable.getSelectionModel().setSelectionInterval(index, index);
			}
		});
		
		fitTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//only for double-clicks
				if (e.getClickCount() != 2) { return; }
				
				//annotation
				TransitionSeries selected = controller.getFittedTransitionSeries().get(fitTable.getSelectedRow());
				
				
				
				JTextField textfield = new JTextField(20);
				textfield.addActionListener(ae -> {
					controller.setAnnotation(selected, textfield.getText());
					plotPanel.popLayer();
				});
				textfield.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
							plotPanel.popLayer();
						}
					}
				});
				textfield.setText(controller.getAnnotation(selected));
				LayerDialog dialog = new LayerDialog("Annotation for " + selected.getDescription(), textfield, MessageType.QUESTION);
				dialog.addLeft(new ImageButton("Cancel").withAction(() -> {
					plotPanel.popLayer();
				}));
				dialog.addRight(new ImageButton("OK").withStateDefault().withAction(() -> {
					controller.setAnnotation(selected, textfield.getText());
					plotPanel.popLayer();
				}));
				dialog.showIn(plotPanel);
				textfield.grabFocus();
				
				
				
			}
		});

		JScrollPane scroll = new JScrollPane(fitTable);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.getViewport().setBackground(Color.white);
		scroll.setBorder(Spacing.bNone());
		
		return scroll;
	}

}
