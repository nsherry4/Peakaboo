package org.peakaboo.ui.swing.calibration.picker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.calibration.CalibrationPluginManager;
import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.framework.swidget.models.ListTableModel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.widgets.ListPickerLayer;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidget;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetCellEditor;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetTableCellRenderer;
import org.peakaboo.ui.swing.calibration.referenceplot.ReferenceViewPanel;

public class ReferencePicker extends ListPickerLayer<CalibrationReference> {

	private LayerPanel parent;
		
	public ReferencePicker(LayerPanel parent, Consumer<CalibrationReference> onAccept) {
		super(parent, "Select Z-Calibration Reference", CalibrationPluginManager.SYSTEM.newInstances(), onAccept);
		this.parent = parent;		
	}
	
	protected JTable getTable(List<CalibrationReference> items) {
		
		TableModel model = new ListTableModel<CalibrationReference>(items) {
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 1;
			}

		};
		
		JTable table = new JTable(model);
		TableColumn cref = table.getColumnModel().getColumn(0);
		TableColumn cmore = table.getColumnModel().getColumn(1);
		cref.setCellRenderer(new ListWidgetTableCellRenderer<>(new ReferenceWidget()));
		cmore.setCellRenderer(new ListWidgetTableCellRenderer<>(new MoreWidget()));
		cmore.setCellEditor(new ListWidgetCellEditor<>(new MoreWidget(this::showReferencePlot)));
		cmore.setWidth(64);
		cmore.setMinWidth(64);
		cmore.setMaxWidth(64);
		cmore.setResizable(false);
		return table;
	}

	private void showReferencePlot(CalibrationReference reference) {
		
		ReferenceViewPanel view = new ReferenceViewPanel(parent, reference);
		parent.pushLayer(view);
		
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
