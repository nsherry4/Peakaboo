package org.peakaboo.ui.swing.plotting.fitting.fitted;



import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonSize;
import org.peakaboo.framework.swidget.widgets.fluent.menuitem.FluentMenuItem;
import org.peakaboo.framework.swidget.widgets.listcontrols.ListControls;
import org.peakaboo.framework.swidget.widgets.listcontrols.ListControls.ElementCount;
import org.peakaboo.framework.swidget.widgets.listcontrols.ReorderTransferHandler;
import org.peakaboo.ui.swing.plotting.PlotPanel;
import org.peakaboo.ui.swing.plotting.fitting.Changeable;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import org.peakaboo.ui.swing.plotting.fitting.MutableTableModel;



public class FittingPanel extends ClearPanel implements Changeable
{

	private ListControls				controls;
	private JTable						fitTable;

	private FittingTreeModel			tm;

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

		FluentButton addButton = new FluentButton(StockIcon.EDIT_ADD).withTooltip("Add Fittings");
		addButton.withAction(() -> addMenu.show(addButton, 0, addButton.getHeight()));
		
		FluentButton removeButton = new FluentButton(StockIcon.EDIT_REMOVE)
			.withTooltip("Remove Selected Fittings")
			.withAction(() -> {
				int[] rows = fitTable.getSelectedRows();
				List<ITransitionSeries> tss = Arrays.stream(rows)
						.boxed()
						.map(i -> controller.getFittedTransitionSeries().get(i))
						.collect(toList());
					
				if (tss.isEmpty()) return;
				for (ITransitionSeries ts : tss)	{
					controller.removeTransitionSeries(ts);
				}
				owner.changed();
		});
		
		FluentButton clearButton = new FluentButton(StockIcon.EDIT_CLEAR)
			.withTooltip("Clear All Fittings")
			.withAction(() -> {
				controller.clearTransitionSeries();
				owner.changed();
		});
		
		controls = new ListControls(addButton, removeButton, clearButton);
		
		FluentButton selectAll = new FluentButton(StockIcon.SELECTION_ALL)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select All")
				.withBordered(false)
				.withAction(() -> controller.setAllTransitionSeriesVisibility(true));
		FluentButton selectNone = new FluentButton(StockIcon.SELECTION_NONE)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select None")
				.withBordered(false)
				.withAction(() -> controller.setAllTransitionSeriesVisibility(false));
		
		controls.addRight(selectNone, ec -> ec != ElementCount.NONE);
		controls.addRight(selectAll, ec -> ec != ElementCount.NONE);
		
		/*
		 * We need to add an extra listener here: Normally we don't update controls when
		 * changes are made because changes are either made locally, or will be updated
		 * automatically (eg TableModel backed by controller). ListControls aren't like
		 * that; when changes are made by something else, we need to update the
		 * controls. Updating the table as well would just cause lost selections, scroll
		 * positions, etc.
		 */
		controller.addListener(t -> updateListControls());
		
		
		this.add(controls, BorderLayout.NORTH);

	}

	private ITransitionSeries getSelected() {		
		int row = fitTable.getSelectedRow();
		if (row == -1) {
			return null;
		}
		if (row >= controller.getFittedTransitionSeries().size()) {
			return null;
		}
		return controller.getFittedTransitionSeries().get(row);
	}
	
	private List<ITransitionSeries> getSelectedList() {
		ITransitionSeries selected = getSelected();
		if (selected == null) {
			return Collections.emptyList();
		} else {
			return Collections.singletonList(selected);
		}
	}

	public void changed() {
		tm.fireChangeEvent();
		updateListControls();
	}

	private void updateListControls() {	
		
		//Which list control buttons to enable?
		int elements = controller.getFittedTransitionSeries().size();
		ElementCount count;
		if (elements == 0) {
			count = ElementCount.NONE;
		}
		else if (elements == 1) {
			count = ListControls.ElementCount.ONE;
		}
		else {
			count = ListControls.ElementCount.MANY;
		}
		controls.setElementCount(count);
		
		
		//Update list selection to match controller selection
		List<ITransitionSeries> modelSelected = controller.getHighlightedTransitionSeries();
		List<ITransitionSeries> viewSelected = getSelectedList();
		if (!modelSelected.equals(viewSelected)) {
			if (modelSelected.isEmpty()) {
				fitTable.getSelectionModel().clearSelection();
			} else {
				//Single selection mode
				ITransitionSeries ts = modelSelected.get(0);
				int index = controller.getFittingSelections().getFittedTransitionSeries().indexOf(ts);
				fitTable.getSelectionModel().setSelectionInterval(index, index);
			}
		}
		
		
		
		
	}
	
	private JPopupMenu createAddMenu() {
		
		JPopupMenu menu = new JPopupMenu();

		JMenuItem elementalAddItem = new FluentMenuItem("Element Lookup").withAction(owner::elementalAdd);
		JMenuItem summationAddItem = new FluentMenuItem("Summation Fitting").withAction(owner::summationAdd);
		JMenuItem smartAddItem = new FluentMenuItem("Guided Fitting").withAction(owner::guidedAdd);
		JMenuItem autoAddItems = new FluentMenuItem("Auto Fitting").withAction(owner::autoAdd);
		
		menu.add(smartAddItem);
		menu.add(autoAddItems);
		menu.add(elementalAddItem);		
		menu.add(summationAddItem);
		
		return menu;
	}


	private JScrollPane createFittedTable()	{

		fitTable = new JTable();
		tm = new FittingTreeModel(controller);
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
			controller.setHighlightedTransitionSeries(getSelectedList());
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
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//only for double-clicks
				if (e.getClickCount() != 2) { return; }
				
				//annotation
				ITransitionSeries selected = controller.getFittedTransitionSeries().get(fitTable.getSelectedRow());
				plotPanel.actionAddAnnotation(selected);

			}
		});

		JScrollPane scroll = new JScrollPane(fitTable);
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.getViewport().setBackground(Color.white);
		scroll.setBorder(Spacing.bNone());
		
		return scroll;
	}

}


class FittingTreeModel implements MutableTableModel {

	List<TableModelListener> listeners;
	private FittingController controller;
	
	public FittingTreeModel(FittingController controller) {
		this.controller = controller;
	}
	

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (columnIndex == 0) {
			Boolean visible = (Boolean) aValue;
			controller.setTransitionSeriesVisibility(
					controller.getFittedTransitionSeries().get(rowIndex),
					visible);
		}

	}


	public void removeTableModelListener(TableModelListener l) {
		if (listeners == null) { listeners = new ArrayList<>(); }
		listeners.remove(l);
	}


	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return controller.getFittedTransitionSeries().get(rowIndex).isVisible();
		} else {
			return controller.getFittedTransitionSeries().get(rowIndex);
		}
	}


	public int getRowCount() {
		return controller.getFittedTransitionSeries().size();
	}


	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) return "Fit";
		return "Transition Series";

	}


	public int getColumnCount() {
		return 2;
	}


	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return Boolean.class;
			case 1:
				return ITransitionSeries.class;
			default:
				return Object.class;
		}
	}


	public void addTableModelListener(TableModelListener l) {
		if (listeners == null) listeners = new ArrayList<>();
		listeners.add(l);
	}


	public void fireChangeEvent() {
		for (TableModelListener l : listeners) {
			l.tableChanged(new TableModelEvent(this));
		}
	}
};

