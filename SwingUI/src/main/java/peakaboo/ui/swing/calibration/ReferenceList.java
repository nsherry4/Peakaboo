package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;

import peakaboo.mapping.calibration.CalibrationPluginManager;
import peakaboo.mapping.calibration.CalibrationReference;
import swidget.widgets.Spacing;

public class ReferenceList extends JPanel {
	
	private JTable table;
	private List<CalibrationReference> refs;
	
	public ReferenceList() {
		this(ref -> {});
	}
	
	public ReferenceList(Consumer<CalibrationReference> onSelect) {
		
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
		
		table.addMouseListener(new MouseAdapter() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					onSelect.accept(getSelectedReference());
				}
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onSelect.accept(getSelectedReference());
				}
			}
		});
		
		
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(scroller.getPreferredSize().width, 250));
		scroller.setBorder(new MatteBorder(1, 1, 1, 1, border));
				
		setLayout(new BorderLayout());
		setBorder(Spacing.bHuge());
		add(scroller, BorderLayout.CENTER);
		
		focusTable();
		
	}
	
	public CalibrationReference getSelectedReference() {
		return refs.get(table.getSelectedRow());
	}
	
	public void focusTable() {
		SwingUtilities.invokeLater(() -> {
			table.requestFocus();
			table.grabFocus();
		});
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


