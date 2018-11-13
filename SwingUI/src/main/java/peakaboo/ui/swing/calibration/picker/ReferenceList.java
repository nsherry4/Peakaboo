package peakaboo.ui.swing.calibration.picker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;

import peakaboo.calibration.CalibrationPluginManager;
import peakaboo.calibration.CalibrationReference;
import peakaboo.ui.swing.plotting.fitting.MutableTableModel;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetCellEditor;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class ReferenceList extends JPanel {
	
	private JTable table;
	private List<CalibrationReference> refs;
	
	public ReferenceList() {
		this(ref -> {}, ref -> {});
	}
	
	public ReferenceList(Consumer<CalibrationReference> onSelect, Consumer<CalibrationReference> onMore) {
		
		refs = CalibrationPluginManager.SYSTEM.getPlugins().newInstances();
		
		TableModel model = new TableModel() {
			
			private List<TableModelListener> listeners = new ArrayList<>();
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				//NA
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
				listeners.remove(l);		
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 1;
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
				if (columnIndex == 0) {
					return "Reference";
				} else {
					return "More";
				}
				
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return CalibrationReference.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
				listeners.add(l);
			}
		};
		table = new JTable(model);
		table.setFillsViewportHeight(true);

		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		
		table.getColumn("Reference").setCellRenderer(new ListWidgetTableCellRenderer<>(new ReferenceWidget()));
		table.getColumn("More").setCellRenderer(new ListWidgetTableCellRenderer<>(new MoreWidget()));
		table.getColumn("More").setCellEditor(new ListWidgetCellEditor<>(new MoreWidget(onMore)));
		table.getColumn("More").setWidth(64);
		table.getColumn("More").setMinWidth(64);
		table.getColumn("More").setMaxWidth(64);
		table.getColumn("More").setResizable(false);
		table.setShowGrid(false);
		table.setGridColor(border);
		table.setTableHeader(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
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
		
		table.setRowSelectionInterval(0, 0);
		
		
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(scroller.getPreferredSize().width, 250));
		scroller.setBorder(new MatteBorder(1, 1, 1, 1, border));
				
		setLayout(new BorderLayout());
		setBorder(Spacing.bHuge());
		add(scroller, BorderLayout.CENTER);
		
		focusTable();
		
	}
		
	public CalibrationReference getSelectedReference() {
		if (table.getSelectedRow() == -1) {
			return null;
		}
		return refs.get(table.getSelectedRow());
	}
	
	public void focusTable() {
		SwingUtilities.invokeLater(() -> {
			table.requestFocus();
			table.grabFocus();
		});
	}

}


class MoreWidget extends ListWidget<CalibrationReference> {
	
	private JLabel label;
	
	public MoreWidget() {
		this(ref -> {});
	}
	
	
	public MoreWidget(Consumer<CalibrationReference> onMore) {
		setLayout(new BorderLayout());
		setBorder(Spacing.bLarge());
		label = new JLabel("more...");
		setLabelColour(getForeground());
		label.setOpaque(false);
		add(label, BorderLayout.SOUTH);
		
		label.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				onMore.accept(getValue());	
			}

		});
		
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (label == null) { return; }
		setLabelColour(c);
	}
	
	private void setLabelColour(Color c) {
		Color cDetail = new Color(c.getRed(), c.getGreen(), c.getBlue(), 192);
		label.setForeground(cDetail);
	}


	@Override
	protected void onSetValue(CalibrationReference value) {
		//It always just says "more..."
	}
}
