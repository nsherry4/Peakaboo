package peakaboo.ui.swing.calibration.concentration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;

import peakaboo.calibration.Concentrations;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import swidget.widgets.Spacing;

public class ConcentrationTablePanel extends JPanel {
	
	
	public ConcentrationTablePanel(Concentrations comp) {
		
		setLayout(new BorderLayout());
		setBorder(Spacing.bHuge());
		
		List<Element> elements = comp.elementsByConcentration();
		AbstractTableModel dm = new AbstractTableModel() {
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Element e = elements.get(rowIndex);
				if (columnIndex == 0) {
					return e.toString();
				} else if (columnIndex == 1) {
					return comp.getRatioFormatted(e);
				} else {
					ITransitionSeries ts = comp.getConcentrationSource(e);
					if (ts == null) {
						return "No Source (!)";
					} else if (!comp.isCalibrated(e)) {
						return "Not Calibrated (!)";
					} else {
						return "Calibrated from " + ts;
					}
				}
			}
			
			@Override
			public int getRowCount() {
				return elements.size();
			}
			
			@Override
			public int getColumnCount() {
				return 3;
			}
			
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) {
					return "Element";
				} else if (columnIndex == 1) {
					return "Ratio to " + comp.getProfile().getReference().getAnchor().getElement().toString();
				} else {
					return "Notes";
				}
			}
			
		};
		
		JTable table = new JTable(dm);
		
		
		table.getColumnModel().getColumn(0).setMaxWidth(120);
		table.getColumnModel().getColumn(0).setMinWidth(120);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		
		table.getColumnModel().getColumn(1).setMaxWidth(120);
		table.getColumnModel().getColumn(1).setMinWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		
		table.setFillsViewportHeight(true);
		
		JScrollPane scroller = new JScrollPane(table);
		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		scroller.setBorder(new MatteBorder(1, 1, 1, 1, border));
		add(scroller, BorderLayout.CENTER);
		
	}

}
