package peakaboo.ui.swing.calibration.referenceplot;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;

import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import stratus.controls.ButtonLinker;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonSize;
import swidget.widgets.buttons.ToggleImageButton;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.layout.HeaderTabBuilder;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class ReferenceViewPanel extends JPanel {

	private HeaderBox header;
	private Runnable onClose;
	
	public ReferenceViewPanel(CalibrationReference reference) {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 350));

		
		JPanel infopanel = new JPanel(new BorderLayout());
		JLabel info = new JLabel("<html>" + reference.pluginDescription() + "</html>");
		info.setVerticalAlignment(SwingConstants.TOP);
		info.setBorder(Spacing.bHuge());
		infopanel.add(info, BorderLayout.CENTER);
		ReferencePlot kplot = new ReferencePlot(reference, TransitionSeriesType.K);
		ReferencePlot lplot = new ReferencePlot(reference, TransitionSeriesType.L);
		ReferencePlot mplot = new ReferencePlot(reference, TransitionSeriesType.M);
		
		HeaderTabBuilder tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab("Details", infopanel);
		tabBuilder.addTab("Alterations", alterations(reference));
		tabBuilder.addTab("K Series", kplot);
		tabBuilder.addTab("L Series", lplot);
		tabBuilder.addTab("M Series", mplot);

		
		this.add(tabBuilder.getBody(), BorderLayout.CENTER);
		
		//header
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE).withTooltip("Close").withBordered(false).withButtonSize(ImageButtonSize.LARGE).withAction(() -> this.onClose.run());
		
		header = new HeaderBox(null, tabBuilder.getTabStrip(), close);
		this.add(header, BorderLayout.NORTH);

		
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
	private JPanel alterations(CalibrationReference reference) {
		
		JPanel alterations = new JPanel(new BorderLayout());
		alterations.setBorder(Spacing.bHuge());
		
		JTable table = new JTable();
		List<TransitionSeries> entries = new ArrayList<>();
		for (TransitionSeriesType type : TransitionSeriesType.values()) {
			for (TransitionSeries ts : reference.getTransitionSeries(type)) {
				if (!reference.hasAnnotation(ts)) { continue; }
				System.out.println(ts);
				entries.add(ts);
			}
		}
		
		TableModel model = new TableModel() {
			
			List<TableModelListener>	listeners = new ArrayList<>();
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				TransitionSeries ts = entries.get(rowIndex);
				if (columnIndex == 0) {
					return ts;
				} else {
					return reference.getAnnotation(ts);
				}
			}
			
			@Override
			public int getRowCount() {
				return entries.size();
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) return "Fitting";
				return "Alteration";
			}
			
			public Class<?> getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 0:
						return TransitionSeries.class;
					case 1:
						return String.class;
					default:
						return Object.class;
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				//NO-OP
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
				listeners.add(l);
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				listeners.remove(l);
			}
			
		};
		
		table.setModel(model);
		
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMinWidth(80);
		column.setPreferredWidth(80);
		column.setMaxWidth(80);
		column.setCellRenderer(new ListWidgetTableCellRenderer<>(new ListWidget<TransitionSeries>() {

			private JLabel label;
			{
				setLayout(new BorderLayout());
				setBorder(Spacing.bSmall());
				label = new JLabel();
				add(label, BorderLayout.CENTER);
				label.setFont(label.getFont().deriveFont(Font.BOLD));
			}
			
			@Override
			public void setForeground(Color c) {
				super.setForeground(c);
				if (label == null) { return; }
				label.setForeground(c);
			}
			
			@Override
			protected void onSetValue(TransitionSeries value) {
				label.setText(value.toString());
			}
		}));
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setFillsViewportHeight(true);
		
		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(new MatteBorder(1, 1, 1, 1, dividerColour));
		alterations.add(scroll, BorderLayout.CENTER);
		
		return alterations;
		
	}
	
	
	
}
