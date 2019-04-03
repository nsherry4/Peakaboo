package org.peakaboo.ui.swing.calibration.referenceplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layout.HeaderTabBuilder;
import org.peakaboo.framework.swidget.widgets.layout.TitledPanel;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidget;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class ReferenceViewPanel extends HeaderLayer {


	public ReferenceViewPanel(LayerPanel parent, CalibrationReference reference) {
		super(parent, true);
		
		getContentRoot().setPreferredSize(new Dimension(700, 350));

		
		JPanel infopanel = new JPanel(new BorderLayout(Spacing.huge, Spacing.huge));
		infopanel.setBorder(Spacing.bHuge());
		
		JLabel info = new JLabel("<html>" + reference.longDescription() + "</html>");
		info.setVerticalAlignment(SwingConstants.TOP);
		infopanel.add(info, BorderLayout.CENTER);
		
		TitledPanel infotitle = new TitledPanel(null, reference.getName(), true);
		infotitle.setBadge(IconFactory.getImageIcon("calibration", IconSize.ICON));
		infopanel.add(infotitle, BorderLayout.NORTH);
		
		ReferencePlot kplot = new ReferencePlot(reference, TransitionShell.K);
		ReferencePlot lplot = new ReferencePlot(reference, TransitionShell.L);
		ReferencePlot mplot = new ReferencePlot(reference, TransitionShell.M);
		
		HeaderTabBuilder tabBuilder = new HeaderTabBuilder();
		tabBuilder.addTab("Details", infopanel);
		tabBuilder.addTab("Alterations", alterations(reference));
		tabBuilder.addTab("K Series", kplot);
		tabBuilder.addTab("L Series", lplot);
		tabBuilder.addTab("M Series", mplot);

		setBody(tabBuilder.getBody());
		getHeader().setCentre(tabBuilder.getTabStrip());
		
	}

	private JPanel alterations(CalibrationReference reference) {
		
		JPanel alterations = new JPanel(new BorderLayout());
		alterations.setBorder(Spacing.bHuge());
		
		JTable table = new JTable();
		List<ITransitionSeries> entries = new ArrayList<>();
		for (TransitionShell type : TransitionShell.values()) {
			for (ITransitionSeries ts : reference.getTransitionSeries(type)) {
				if (!reference.hasAnnotation(ts)) { continue; }
				entries.add(ts);
			}
		}
		
		TableModel model = new TableModel() {
			
			List<TableModelListener>	listeners = new ArrayList<>();
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				ITransitionSeries ts = entries.get(rowIndex);
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
						return ITransitionSeries.class;
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
		column.setCellRenderer(new ListWidgetTableCellRenderer<>(new ListWidget<ITransitionSeries>() {

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
			protected void onSetValue(ITransitionSeries value) {
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
