package peakaboo.ui.swing.plotting.filters;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import peakaboo.controller.plotter.FilterController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.filters.AbstractFilter;
import swidget.containers.SwidgetContainer;
import swidget.widgets.ClearPanel;
import swidget.widgets.listcontrols.ListControls;


public class FilterEditViewer extends ClearPanel{

	
	protected FilterController controller;
	protected FiltersetViewer owner;
	
	protected JTable t;
	private TableModel m;
	
	protected ListControls controls;
	
	public FilterEditViewer(FilterController _controller, SwidgetContainer windowOwner, FiltersetViewer _owner){
		
		super();
		
		controller = _controller;
		owner = _owner;
		
		setLayout(new BorderLayout());
		
		JScrollPane scroller = new JScrollPane(createFilterTable(windowOwner), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		add(scroller, BorderLayout.CENTER);
		//add(createControlPanel(), BorderLayout.SOUTH);
		//add(new TitleGradientPanel("Data Filters", true, createControlPanel()), BorderLayout.NORTH);
		add(createControlPanel(), BorderLayout.NORTH);
		
		controller.addListener(new PeakabooSimpleListener() {
			
			public void change() {

				t.invalidate();
				
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
	
	private JTable createFilterTable(SwidgetContainer owner){
		
		m = new TableModel() {
		
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				
				if (columnIndex == 0){
					boolean enabled = (Boolean)value;
					controller.setFilterEnabled(rowIndex, enabled);
				}
				
			}
		
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
		
			}
		
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex <= 1) return true;
				return false;
			}
		
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0) return controller.getFilterEnabled(rowIndex);
				return controller.getFilter(rowIndex);
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
				return AbstractFilter.class;
			}
		
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
		
			}
		};
		
		t = new JTable(m);
						
		t.getColumnModel().getColumn(1).setCellRenderer(new FilterEditButtonRenderer());
		t.getColumnModel().getColumn(1).setCellEditor(new FilterEditButtonEditor(controller, owner));
		
		t.getColumnModel().getColumn(2).setCellRenderer(new FilterRenderer());
		//t.setRowHeight(10);
		
		t.setShowVerticalLines(false);
		t.setShowHorizontalLines(false);
		
		TableColumn column = t.getColumnModel().getColumn(0);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);
		
		column = t.getColumnModel().getColumn(1);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);
		
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		return t;
		
	}


	private JPanel createControlPanel(){
		
		controls = new ListControls(null) {
		
			@Override
			public void up()
			{
				AbstractFilter selection = controller.getFilter(t.getSelectedRow());
				controller.moveFilterUp(t.getSelectedRow());
				int selRow = controller.filterIndex(selection);
				t.addRowSelectionInterval(selRow, selRow);
			}
		
			
			@Override
			public void down()
			{
				AbstractFilter selection = controller.getFilter(t.getSelectedRow());
				controller.moveFilterDown(t.getSelectedRow());
				int selRow = controller.filterIndex(selection);
				t.addRowSelectionInterval(selRow, selRow);
			}
			
			
			@Override
			public void remove()
			{
				controller.removeFilter(t.getSelectedRow());
			}
		

			@Override
			public void clear()
			{
				controller.clearFilters();
			}
		
		
			@Override
			public void add()
			{
				owner.showSelectPane();
			}
		};
		
		return controls;
		
		
	}
	
}
