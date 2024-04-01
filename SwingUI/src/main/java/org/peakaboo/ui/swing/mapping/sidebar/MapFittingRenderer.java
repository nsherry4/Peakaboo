package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.Component;
import java.util.function.Predicate;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionSeriesMode;
import org.peakaboo.ui.swing.plotting.fitting.fitted.FittedWidget;

public class MapFittingRenderer extends DefaultTableCellRenderer
{

	private FittedWidget tswidget;
	private Predicate<ITransitionSeries> tsEnabled;
	
	
	public MapFittingRenderer(Predicate<ITransitionSeries> enabled){
		tswidget = FittedWidget.medium();	
		this.tsEnabled = enabled;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus,
			int row, int column)
	{
		
		super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		

		if (selected){	
			tswidget.setOpaque(true);
			tswidget.setBackground(table.getSelectionBackground());
			tswidget.setForeground(table.getSelectionForeground());
		} else {
			tswidget.setOpaque(false);
			tswidget.setBackground(table.getBackground());
			tswidget.setForeground(table.getForeground());
		}
		
		
		if (table.getRowHeight() < tswidget.getPreferredSize().height) {
			table.setRowHeight(tswidget.getPreferredSize().height);
		}
		
		if (value instanceof ITransitionSeries ts){
			tswidget.setName(ts.toString());
			tswidget.setEnabled(tsEnabled.test(ts));
			
			String tooltip = "";
			if (ts.getMode() != TransitionSeriesMode.SUMMATION){
				tooltip = ts.getElement().toString();
			} else {
				tooltip = ts.getPrimaryTransitionSeries()
						.stream()
						.map(ITransitionSeries::getElement)
						.map(Element::toString)
						.reduce((a, b) -> a + ", " + b)
						.orElse("");
			}
			tswidget.setToolTipText(tooltip);
			
			return tswidget;
		} 
		
		return this;
	}
	

}