package peakaboo.ui.swing.calibration.composition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;

import peakaboo.calibration.Composition;
import peakaboo.curvefit.peak.table.Element;
import swidget.widgets.Spacing;

public class CompositionTablePanel extends JPanel {
	
	
	public CompositionTablePanel(Composition comp) {
		
		setLayout(new BorderLayout());
		setBorder(Spacing.bHuge());
		
		List<Element> elements = comp.elementsByConcentration();
		AbstractTableModel dm = new AbstractTableModel() {
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Element e = elements.get(rowIndex);
				if (columnIndex == 0) {
					return e.toString();
				} else {
					return comp.getPercent(e);
				}
			}
			
			@Override
			public int getRowCount() {
				return elements.size();
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0) {
					return "Element";
				} else {
					return "Composition";
				}
			}
			
		};
		
		JTable table = new JTable(dm);
		
		
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.setFillsViewportHeight(true);
		
		JScrollPane scroller = new JScrollPane(table);
		Color border = UIManager.getColor("stratus-widget-border");
		if (border == null) { border = Color.LIGHT_GRAY; }
		scroller.setBorder(new MatteBorder(1, 1, 1, 1, border));
		add(scroller, BorderLayout.CENTER);
		
	}

}
