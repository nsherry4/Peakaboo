package peakaboo.ui.swing.plotting.filters;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.peakaboo.controller.plotter.filtering.FilteringController;
import org.peakaboo.filter.model.Filter;

import eventful.EventfulListener;
import peakaboo.ui.swing.plotting.fitting.MutableTableModel;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.listcontrols.ListControls;
import swidget.widgets.listcontrols.ReorderTransferHandler;


class FilterList extends ClearPanel {

	
	private FilteringController controller;
	private FiltersetViewer owner;
	
	private JTable t;
	private MutableTableModel m;
	
	private ListControls controls;
	
	FilterList(FilteringController _controller, Window ownerWindow, FiltersetViewer _owner){
		
		super();
		
		controller = _controller;
		owner = _owner;
		
		setLayout(new BorderLayout());
		
		JTable table = createFilterTable(ownerWindow);
		JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(Spacing.bNone());
		
		add(scroller, BorderLayout.CENTER);
		//add(createControlPanel(), BorderLayout.SOUTH);
		//add(new TitleGradientPanel("Data Filters", true, createControlPanel()), BorderLayout.NORTH);
		add(createControlPanel(), BorderLayout.NORTH);
		
		controller.addListener(new EventfulListener() {
			
			public void change() {

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
					
				
			}
		});
		
		
	}
	
	private JTable createFilterTable(Window owner){
		
		m = new MutableTableModel() {
		
			List<TableModelListener> listeners;
		
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0){
					boolean enabled = (Boolean)value;
					controller.setFilterEnabled(rowIndex, enabled);
				}
				
			}
		
			public void removeTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = new ArrayList<TableModelListener>();
				listeners.remove(l);
			}
		
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex <= 1) return true;
				return false;
			}
		
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0) return controller.getFilterEnabled(rowIndex);
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
				if (columnIndex == 0) return Boolean.class;
				return Filter.class;
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
		
		t = new JTable(m);
		t.setFillsViewportHeight(true);
				
		t.getColumnModel().getColumn(1).setCellRenderer(new EditButtonRenderer());
		t.getColumnModel().getColumn(1).setCellEditor(new EditButtonEditor(controller, owner));
		
		t.getColumnModel().getColumn(2).setCellRenderer(new FilterRenderer());
		//t.setRowHeight(10);
		
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
		column.setMinWidth(45);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);
		
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
		
		ImageButton addButton = new ImageButton(StockIcon.EDIT_ADD).withTooltip("Add Filter").withAction(() -> {
			owner.showSelectPane();
		});
		
		ImageButton removeButton = new ImageButton(StockIcon.EDIT_REMOVE).withTooltip("Remove Selected Filter").withAction(() -> {
			if (t.getSelectedRow() == -1) return;
			controller.removeFilter(t.getSelectedRow());
		});
		
		ImageButton clearButton = new ImageButton(StockIcon.EDIT_CLEAR).withTooltip("Clear All Filters").withAction(() -> {
			controller.clearFilters();
		});
		
		controls = new ListControls(addButton, removeButton, clearButton);
		
		
		return controls;
		
		
	}
	
}
