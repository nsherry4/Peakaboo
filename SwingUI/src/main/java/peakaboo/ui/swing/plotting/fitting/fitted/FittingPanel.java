package peakaboo.ui.swing.plotting.fitting.fitted;



import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.fitting.MutableTableModel;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.listcontrols.ListControls;



public class FittingPanel extends ClearPanel implements Changeable
{

	private ListControls				controls;
	private JTable						fitTable;

	private MutableTableModel			tm;

	private CurveFittingView			owner;
	private FittingController			controller;


	public FittingPanel(final FittingController controller, final CurveFittingView owner)
	{

		this.owner = owner;
		this.controller = controller;
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());

		JScrollPane fitted = createFittedTable();
		this.add(fitted, BorderLayout.CENTER);

		JPopupMenu addMenu = createAddMenu();

		String tooltips[] = {
				"Add fittings",
				"Remove the selected fittings",
				"Clear all fittings",
				"Move the selected fittings up",
				"Move the selected fittings down" };
		controls = new ListControls(tooltips, addMenu, true) {

			@Override
			public void up()
			{

				int rows[] = fitTable.getSelectedRows();
				List<TransitionSeries> tss = Arrays.stream(rows).boxed().map(i -> controller.getFittedTransitionSeries().get(i)).collect(toList());
					
				if (tss.size() == 0) return;
				
				controller.moveTransitionSeriesUp(tss);
				owner.changed();
				for (TransitionSeries ts : tss) {
					int row = controller.getFittedTransitionSeries().indexOf(ts);
					fitTable.addRowSelectionInterval(row, row);
				}
			}


			@Override
			public void remove()
			{
				
				int rows[] = fitTable.getSelectedRows();
				List<TransitionSeries> tss = Arrays.stream(rows).boxed().map(i -> controller.getFittedTransitionSeries().get(i)).collect(toList());
					
				if (tss.size() == 0) return;
				for (TransitionSeries ts : tss)
				{
					controller.removeTransitionSeries(ts);
				}
				owner.changed();
					
			}


			@Override
			public void down()
			{
				int rows[] = fitTable.getSelectedRows();
				List<TransitionSeries> tss = Arrays.stream(rows).boxed().map(i -> controller.getFittedTransitionSeries().get(i)).collect(toList());
					
				if (tss.size() == 0) return;
				
				controller.moveTransitionSeriesDown(tss);
				owner.changed();
				for (TransitionSeries ts : tss) {
					int row = controller.getFittedTransitionSeries().indexOf(ts);
					fitTable.addRowSelectionInterval(row, row);
				}
			}


			@Override
			public void clear()
			{
				controller.clearTransitionSeries();
				owner.changed();
			}


			@Override
			public void add()
			{
				//Not used when the add button it set to show it's popup menu
				//owner.smartAdd();
			}

		};

	
		
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

		menu.add(smartAddItem);
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

		fitTable.setRowHeight(renderer.getPreferredSize().height);

		TableColumn column = fitTable.getColumnModel().getColumn(0);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		fitTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
		
		JScrollPane scroll = new JScrollPane(fitTable);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.getViewport().setBackground(Color.white);
		scroll.setBorder(Spacing.bNone());
		
		return scroll;
	}

}
