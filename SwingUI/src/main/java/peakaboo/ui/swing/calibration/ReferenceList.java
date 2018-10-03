package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import peakaboo.mapping.calibration.CalibrationPluginManager;
import peakaboo.mapping.calibration.CalibrationReference;
import stratus.StratusLookAndFeel;
import swidget.widgets.Spacing;

public class ReferenceList extends JPanel {
	
	private JTable table;
	private List<CalibrationReference> refs;
	
	public ReferenceList() {
		
		refs = CalibrationPluginManager.SYSTEM.getPlugins().newInstances();
		
		TableModel model = new TableModel() {
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				//NA
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
				//TODO?			
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return refs.get(rowIndex);
			}
			
			@Override
			public int getRowCount() {
				return refs.size();
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return "Calibration Reference";
			}
			
			@Override
			public int getColumnCount() {
				return 1;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return CalibrationReference.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
				//TODO?
			}
		};
		table = new JTable(model);
		table.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		table.getSelectionModel().setSelectionInterval(0, 0);
		

		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		
		TableCellRenderer renderer = new ReferenceRenderer();
		table.setDefaultRenderer(CalibrationReference.class, renderer);
		table.setShowGrid(true);
		table.setGridColor(border);
		table.setTableHeader(null);
		
		
		
		
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(scroller.getPreferredSize().width, 250));
		scroller.setBorder(new MatteBorder(1, 1, 1, 1, border));
				
		setLayout(new BorderLayout());
		setBorder(Spacing.bHuge());
		add(scroller, BorderLayout.CENTER);
		
	}
	
	public CalibrationReference getSelectedReference() {
		return refs.get(table.getSelectedRow());
	}
	

	
}
class ReferenceRenderer implements TableCellRenderer {

	private ReferenceWidget widget;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		if (widget == null) {
			Color border = UIManager.getColor("stratus-widget-border");
			if (border == null) { border = Color.LIGHT_GRAY; }
			widget = new ReferenceWidget();
			//widget.setBorder(new MatteBorder(0, 0, 1, 0, border));
		}
		
		widget.setReference((CalibrationReference) value);
		if (isSelected) {
			widget.setBackground(table.getSelectionBackground());
			widget.setForeground(table.getSelectionForeground());
			widget.setOpaque(true);
		} else {
			widget.setBackground(table.getBackground());
			widget.setForeground(table.getForeground());
			widget.setOpaque(false);
		}
		
		if (table.getRowHeight() < widget.getPreferredSize().height) {
			table.setRowHeight(widget.getPreferredSize().height);
		}
		
		return widget;
		
	}
}


