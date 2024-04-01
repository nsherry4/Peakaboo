package org.peakaboo.ui.swing.plotting.filters;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.itemlist.ListControls;
import org.peakaboo.framework.stratus.components.ui.itemlist.ReorderTransferHandler;
import org.peakaboo.ui.swing.plotting.fitting.MutableTableModel;


class FilterList extends ClearPanel {

	
	private FilteringController controller;
	private FiltersetViewer filtersUI;
	
	private JTable t;
	private MutableTableModel m;
	
	private ListControls controls;
	
	FilterList(FilteringController filteringController, Window ownerWindow, FiltersetViewer filtersUI) {
		
		super();
		
		this.controller = filteringController;
		this.filtersUI = filtersUI;
		
		setLayout(new BorderLayout());
		
		JTable table = createFilterTable(ownerWindow);
		JScrollPane scroller = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(Spacing.bNone());
		
		add(scroller, BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.NORTH);
		
		controller.addListener(() -> {
			t.invalidate();
			m.fireChangeEvent();
			
			int elements = controller.getFilterCount();
			
			if ( elements == 0 ){
				controls.setElementCount(ListControls.ElementCount.NONE);
			} else if ( elements == 1 ){
				controls.setElementCount(ListControls.ElementCount.ONE);
			} else {
				controls.setElementCount(ListControls.ElementCount.MANY);
			}
		});
		
		
	}
	
	private JTable createFilterTable(Window owner){
		
		m = new FilterTableModel(controller);
		
		t = new JTable(m);
		t.setFillsViewportHeight(true);
				
		t.getColumnModel().getColumn(1).setCellRenderer(new EditButtonRenderer());
		t.getColumnModel().getColumn(1).setCellEditor(new EditButtonEditor(controller, owner, this.filtersUI));
		
		t.getColumnModel().getColumn(2).setCellRenderer(new FilterRenderer());
		
		t.setShowVerticalLines(false);
		t.setShowHorizontalLines(false);
		t.setShowGrid(false);
		t.setTableHeader(null);
		
		//USE column
		TableColumn column = t.getColumnModel().getColumn(0);
		column.setResizable(false);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(40);
		
		//EDIT column
		column = t.getColumnModel().getColumn(1);
		column.setResizable(false);
		column.setMinWidth(28);
		column.setPreferredWidth(28);
		column.setMaxWidth(28);
		
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		t.setDragEnabled(true);
		t.setDropMode(DropMode.INSERT_ROWS);
		t.setTransferHandler(new ReorderTransferHandler(t) {
			
			@Override
			public void move(int from, int to) {
				controller.moveFilter(from, to);
			}
		});
		
		ToolTipManager.sharedInstance().registerComponent(t);
		
		return t;
		
	}


	private JPanel createControlPanel(){
		
		FluentButton addButton = new FluentButton()
				.withIcon(StockIcon.EDIT_ADD, Stratus.getTheme().getControlText())
				.withTooltip("Add Filter")
				.withAction(() -> filtersUI.showSelectPane());
		
		FluentButton removeButton = new FluentButton()
				.withIcon(StockIcon.EDIT_REMOVE, Stratus.getTheme().getControlText())
				.withTooltip("Remove Selected Filter")
				.withAction(() -> {
					if (t.getSelectedRow() != -1) { controller.removeFilter(t.getSelectedRow()); }
				});
		
		FluentButton clearButton = new FluentButton()
				.withIcon(StockIcon.EDIT_CLEAR, Stratus.getTheme().getControlText())
				.withTooltip("Clear All Filters")
				.withAction(() -> controller.clearFilters());
		
		controls = new ListControls(addButton, removeButton, clearButton);
		
		
		return controls;
		
		
	}
	
}


class FilterTableModel implements MutableTableModel {
	
	private List<TableModelListener> listeners;
	private FilteringController controller;
	
	public FilterTableModel(FilteringController controller) {
		this.controller = controller;
	}
	
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		
		if (columnIndex == 0) {
			boolean enabled = (Boolean)value;
			controller.setFilterEnabled(rowIndex, enabled);
		}
		
	}

	public void removeTableModelListener(TableModelListener l) {
		if (listeners == null) { listeners = new ArrayList<>(); }
		listeners.remove(l);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex <= 1) { return true; }
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) { return controller.getFilterEnabled(rowIndex); }
		return controller.getActiveFilter(rowIndex);
	}

	public int getRowCount() {
		return controller.getFilterCount();
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) return "Use";
		if (columnIndex == 1) return "Edit";
		return "Filter Order";
	}

	public int getColumnCount() {
		return 3;
	}

	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) { return Boolean.class; }
		return Filter.class;
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
